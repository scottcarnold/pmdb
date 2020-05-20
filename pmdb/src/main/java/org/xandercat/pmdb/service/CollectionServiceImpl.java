package org.xandercat.pmdb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xandercat.pmdb.dao.CollectionDao;
import org.xandercat.pmdb.dao.KeyGenerator;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dao.repository.DynamoCollectionRepository;
import org.xandercat.pmdb.dao.repository.DynamoMovieRepository;
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CloudServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.util.ExcelPorter;

@Component
public class CollectionServiceImpl implements CollectionService {

	private static final Logger LOGGER = LogManager.getLogger(CollectionServiceImpl.class);
	
	@Autowired
	private CollectionDao collectionDao;

	@Autowired
	private DynamoCollectionRepository dynamoCollectionRepository;
	
	@Autowired
	private MovieDao movieDao;
	
	@Autowired
	private DynamoMovieRepository dynamoMovieRepository;
	
	@Autowired
	private KeyGenerator keyGenerator;
	
	@Value("${aws.enable:false}")
	private boolean awsEnabled;
	
	private void assertCloudReady(MovieCollection movieCollection) throws CloudServicesException {
		if (!awsEnabled && movieCollection.isCloud()) {
			throw new CloudServicesException("Cloud services are disabled.");
		}
		return;
	}
	
	@Override
	public MovieCollection getDefaultMovieCollection(String username) {
		String collectionId = collectionDao.getDefaultCollectionId(username);
		return (collectionId == null)? null : collectionDao.getViewableMovieCollection(collectionId, username);
	}

	@Override
	public void setDefaultMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException {
		assertCollectionViewable(collectionId, callingUsername);		
		collectionDao.setDefaultCollection(callingUsername, collectionId);
		
	}

	@Override
	public List<MovieCollection> getViewableMovieCollections(String username) {
		return collectionDao.getViewableMovieCollections(username);
	}

	@Override
	public List<MovieCollection> getShareOfferMovieCollections(String username) {
		return collectionDao.getShareOfferMovieCollections(username);
	}

	@Override
	public MovieCollection getViewableMovieCollection(String collectionId, String callingUsername)	throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null) {
			throw new CollectionSharingException("User does not have permission to view collection.");
		}
		return movieCollection;
	}

	@Override
	public void addMovieCollection(MovieCollection movieCollection, String callingUsername) throws CloudServicesException {
		movieCollection.setOwner(callingUsername, callingUsername); // enforce that movie collection owner is the calling username
		assertCloudReady(movieCollection);
		//TODO: Need to consider error control, especially considering the mirroring of movie collections (check other methods too)
		collectionDao.addMovieCollection(movieCollection);
		if (movieCollection.isCloud()) {
			try {
				dynamoCollectionRepository.save(movieCollection);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		}
	}

	@Override
	public void updateMovieCollection(MovieCollection movieCollection, String callingUsername) throws CollectionSharingException, CloudServicesException {
		MovieCollection viewableMovieCollection = collectionDao.getViewableMovieCollection(movieCollection.getId(), callingUsername);
		if (viewableMovieCollection == null || !viewableMovieCollection.isEditable()) { // collection must be editable
			throw new CollectionSharingException("User does not have required permission to update collection.");
		}
		assertCloudReady(viewableMovieCollection);
		viewableMovieCollection.setName(movieCollection.getName());
		collectionDao.updateMovieCollection(viewableMovieCollection);
		if (viewableMovieCollection.isCloud()) {
			try {
				dynamoCollectionRepository.save(viewableMovieCollection);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		}
	}

	@Override
	public void deleteMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException, CloudServicesException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !movieCollection.getOwner().equals(callingUsername)) { // only owner can delete collection
			throw new CollectionSharingException("User does not have required permission to delete collection.");
		}
		assertCloudReady(movieCollection);
		if (movieCollection.isCloud()) {
			try {
				dynamoMovieRepository.deleteByCollectionId(collectionId);
				dynamoCollectionRepository.deleteById(collectionId);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			movieDao.deleteMoviesForCollection(collectionId);
		}
		collectionDao.deleteMovieCollection(collectionId);
	}

	@Override
	public void shareMovieCollection(String collectionId, String shareWithUsername, boolean editable, String callingUsername) throws CollectionSharingException {
		assertCollectionEditable(collectionId, callingUsername);
		collectionDao.shareCollection(collectionId, shareWithUsername, editable);
	}

	@Override
	public void unshareMovieCollection(String collectionId, String unshareWithUsername, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = null;
		if (callingUsername.equals(unshareWithUsername)) {
			movieCollection = assertCollectionViewable(collectionId, callingUsername);
		} else {
			movieCollection = assertCollectionEditable(collectionId, callingUsername);
		}
		if (movieCollection.getOwner().equals(unshareWithUsername)) {
			throw new CollectionSharingException("Collection owner permissions cannot be revoked.");
		}
		collectionDao.unshareCollection(collectionId, unshareWithUsername);
	}

	@Override
	public void updateEditable(String collectionId, String updateUsername, boolean editable, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = assertCollectionEditable(collectionId, callingUsername);
		if (movieCollection.getOwner().equals(updateUsername)) {
			throw new CollectionSharingException("Collection owner permissions cannot be changed.");
		}
		collectionDao.updateEditable(collectionId, updateUsername, editable);
		
	}

	@Override
	public void acceptShareOffer(String collectionId, String callingUsername) throws CollectionSharingException {
		if (!collectionDao.acceptShareOffer(collectionId, callingUsername)) {
			throw new CollectionSharingException("Unable to accept share offer for collection " + collectionId + " user " + callingUsername);
		}
	}

	@Override
	public void declineShareOffer(String collectionId, String callingUsername) throws CollectionSharingException {
		if (!collectionDao.unshareCollection(collectionId, callingUsername)) {
			throw new CollectionSharingException("Unable to decline share offer for collection " + collectionId + " user " + callingUsername);
		}
	}
	
	@Override
	public MovieCollection assertCollectionViewable(String collectionId, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null) { // collection must be viewable
			throw new CollectionSharingException("User does not have required permission to update share permission.");
		}
		return movieCollection;
	}

	@Override
	public MovieCollection assertCollectionEditable(String collectionId, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !movieCollection.isEditable()) { // collection must be editable
			throw new CollectionSharingException("User does not have required permission to update share permission.");
		}
		return movieCollection;
	}

	@Override
	public List<CollectionPermission> getCollectionPermissions(String collectionId, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !callingUsername.equals(movieCollection.getOwner())) {
			throw new CollectionSharingException("User can only view sharing permissions of collections they are the owner of.");
		}
		return collectionDao.getCollectionPermissions(collectionId);
	}

	@Override
	public CollectionPermission getCollectionPermission(String collectionId, String username, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !callingUsername.equals(movieCollection.getOwner())) {
			throw new CollectionSharingException("User can only view sharing permissions of collections they are the owner of.");
		}
		return collectionDao.getCollectionPermission(collectionId, username);
	}

	@Override
	public void importCollection(MultipartFile mFile, String collectionName, boolean cloud, List<String> sheetNames, List<String> columnNames, String callingUsername) throws IOException {
		long startTime = System.currentTimeMillis();
		ExcelPorter excelImporter = new ExcelPorter(mFile.getInputStream(), mFile.getOriginalFilename());
		List<Movie> movies = new ArrayList<Movie>();
		for (String sheetName : sheetNames) {
			movies.addAll(excelImporter.getMoviesForSheet(sheetName, columnNames));
		}
		long parseTime = System.currentTimeMillis();
		MovieCollection movieCollection = new MovieCollection();
		movieCollection.setName(collectionName);
		movieCollection.setOwner(callingUsername, callingUsername);
		movieCollection.setEditable(true);
		movieCollection.setCloud(cloud);
		collectionDao.addMovieCollection(movieCollection);
		if (cloud) {
			dynamoCollectionRepository.save(movieCollection); // key will have already been set by mirrored movie collection save
		}
		for (Movie movie : movies) {
			movie.setCollectionId(movieCollection.getId());
		}
		if (cloud) {
			for (Movie movie : movies) {
				movie.setId(keyGenerator.getKey());  // TODO: Could redefine the DynamoDB table to have auto-generated keys
			}
			dynamoMovieRepository.saveAll(movies);
		} else {
			movieDao.addMovies(movies);
		}
		long storeTime = System.currentTimeMillis();
		LOGGER.info("Time to parse: " + String.valueOf(parseTime - startTime) + "ms; Time to store: " + String.valueOf(storeTime - parseTime) + " ms");
	}
}
