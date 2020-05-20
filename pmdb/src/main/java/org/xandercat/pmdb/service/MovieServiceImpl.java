package org.xandercat.pmdb.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.KeyGenerator;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dao.repository.DynamoMovieRepository;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CloudServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.PmdbException;

@Component
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieDao movieDao;

	@Autowired
	private DynamoMovieRepository dynamoMovieRepository;
	
	@Autowired
	private CollectionService collectionService;
	
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
	public Set<Movie> getMoviesForCollection(String collectionId, String callingUsername) throws CollectionSharingException, CloudServicesException {
		MovieCollection movieCollection = collectionService.assertCollectionViewable(collectionId, callingUsername);
		assertCloudReady(movieCollection);
		if (movieCollection.isCloud()) {
			try {
				Set<Movie> movies = new HashSet<Movie>();
				movies.addAll(dynamoMovieRepository.findByCollectionId(collectionId));
				return movies;
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			return movieDao.getMoviesForCollection(collectionId);
		}
	}

	@Override
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString, String callingUsername) throws CollectionSharingException, CloudServicesException {
		MovieCollection movieCollection = collectionService.assertCollectionViewable(collectionId, callingUsername);
		assertCloudReady(movieCollection);
		if (movieCollection.isCloud()) {
			try {
				return dynamoMovieRepository.searchMoviesForCollection(collectionId, searchString);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			return movieDao.searchMoviesForCollection(collectionId, searchString);
		}		
	}

	@Override
	public Movie getMovie(String id, String callingUsername) throws CollectionSharingException, CloudServicesException {
		// doing a blind retrieve here; if not in local db, then try AWS
		Movie movie = movieDao.getMovie(id);
		if ((movie == null) && awsEnabled) {
			try {
				Optional<Movie> optional = dynamoMovieRepository.findById(id);
				if (optional.isPresent()) {
					movie = optional.get();
				}
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		}
		if (movie != null) {
			collectionService.assertCollectionViewable(movie.getCollectionId(), callingUsername);
		}
		return movie;
	}

	@Override
	public void addMovie(Movie movie, String callingUsername) throws CollectionSharingException, CloudServicesException {
		MovieCollection movieCollection = collectionService.assertCollectionEditable(movie.getCollectionId(), callingUsername);
		assertCloudReady(movieCollection);
		if (movieCollection.isCloud()) {
			try {
				movie.setId(keyGenerator.getKey());
				dynamoMovieRepository.save(movie);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			movieDao.addMovie(movie);
		}
	}

	@Override
	public void updateMovie(Movie movie, String callingUsername) throws CollectionSharingException, CloudServicesException {
		MovieCollection movieCollection = collectionService.assertCollectionEditable(movie.getCollectionId(), callingUsername);
		assertCloudReady(movieCollection);
		if (movieCollection.isCloud()) {
			try {
				dynamoMovieRepository.save(movie);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			movieDao.updateMovie(movie);
		}
	}

	@Override
	public void deleteMovie(String id, String callingUsername) throws CollectionSharingException, CloudServicesException {
		// doing a blind retrieve here; if not in local db, then try AWS
		Movie movie = movieDao.getMovie(id);
		if ((movie == null) && awsEnabled) {
			try {
				Optional<Movie> optional = dynamoMovieRepository.findById(id);
				if (optional.isPresent()) {
					movie = optional.get();
				}
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		}		
		MovieCollection movieCollection = collectionService.assertCollectionEditable(movie.getCollectionId(), callingUsername);
		if (movieCollection.isCloud()) {
			try {
				dynamoMovieRepository.delete(movie);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			movieDao.deleteMovie(id);
		}
	}

	@Override
	public List<String> getTableColumnPreferences(String callingUsername) {
		return movieDao.getTableColumnPreferences(callingUsername);
	}

	@Override
	public void addTableColumnPreference(String attributeName, String callingUsername) {
		movieDao.addTableColumnPreference(attributeName, callingUsername);
		
	}

	@Override
	public void reorderTableColumnPreference(int sourceIdx, int targetIdx, String callingUsername) throws PmdbException {
		if (sourceIdx == targetIdx) {
			return;
		}
		Integer max = movieDao.getMaxTableColumnPreferenceIndex(callingUsername);
		if (max == null) {
			throw new PmdbException("User has no preferences to reorder.");
		}
		if (sourceIdx < 0 || targetIdx < 0 || sourceIdx > max || targetIdx > max) {
			throw new PmdbException("source index or target index do not fall in acceptable range of 0 to " + max);
		}
		movieDao.reorderTableColumnPreference(sourceIdx, targetIdx, callingUsername);
	}

	@Override
	public void deleteTableColumnPreference(int sourceIdx, String callingUsername) {
		movieDao.deleteTableColumnPreference(sourceIdx, callingUsername);
	}

	@Override
	public List<String> getAttributeKeysForCollection(String collectionId, String callingUsername) throws CollectionSharingException, CloudServicesException {
		MovieCollection movieCollection = collectionService.assertCollectionViewable(collectionId, callingUsername);
		List<String> attributeKeys = null;
		assertCloudReady(movieCollection);
		if (movieCollection.isCloud()) {
			try {
				attributeKeys = dynamoMovieRepository.getAttributeKeysForCollection(collectionId);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			attributeKeys = movieDao.getAttributeKeysForCollection(collectionId);
		}
		Collections.sort(attributeKeys);
		return attributeKeys;
	}

	@Override
	public Set<String> getImdbIdsInDefaultCollection(String callingUsername) throws CloudServicesException {
		MovieCollection defaultMovieCollection = collectionService.getDefaultMovieCollection(callingUsername);
		assertCloudReady(defaultMovieCollection);
		if (defaultMovieCollection.isCloud()) {
			try {
				return dynamoMovieRepository.getAttributeValuesForCollection(defaultMovieCollection.getId(), ImdbSearchService.IMDB_ID_KEY);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			return movieDao.getAttributeValuesForCollection(defaultMovieCollection.getId(), ImdbSearchService.IMDB_ID_KEY);
		}		
	}
}
