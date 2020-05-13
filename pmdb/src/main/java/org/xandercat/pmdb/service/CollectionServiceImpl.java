package org.xandercat.pmdb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.CollectionDao;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CollectionSharingException;

@Component
public class CollectionServiceImpl implements CollectionService {

	@Autowired
	private CollectionDao collectionDao;
	
	@Autowired
	private MovieDao movieDao;
	
	@Override
	public MovieCollection getDefaultMovieCollection(String username) {
		Integer collectionId = collectionDao.getDefaultCollectionId(username);
		return (collectionId == null)? null : collectionDao.getViewableMovieCollection(collectionId, username);
	}

	@Override
	public void setDefaultMovieCollection(int collectionId, String callingUsername) throws CollectionSharingException {
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
	public MovieCollection getViewableMovieCollection(int collectionId, String callingUsername)	throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null) {
			throw new CollectionSharingException("User does not have permission to view collection.");
		}
		return movieCollection;
	}

	@Override
	public void addMovieCollection(MovieCollection movieCollection, String callingUsername) {
		movieCollection.setOwner(callingUsername); // enforce that movie collection owner is the calling username
		collectionDao.addMovieCollection(movieCollection);
	}

	@Override
	public void updateMovieCollection(MovieCollection movieCollection, String callingUsername) throws CollectionSharingException {
		MovieCollection viewableMovieCollection = collectionDao.getViewableMovieCollection(movieCollection.getId(), callingUsername);
		if (viewableMovieCollection == null || !viewableMovieCollection.isEditable()) { // collection must be editable
			throw new CollectionSharingException("User does not have required permission to update collection.");
		}
		viewableMovieCollection.setName(movieCollection.getName());
		collectionDao.updateMovieCollection(viewableMovieCollection);
	}

	@Override
	public void deleteMovieCollection(int collectionId, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !movieCollection.getOwner().contentEquals(callingUsername)) { // only owner can delete collection
			throw new CollectionSharingException("User does not have required permission to delete collection.");
		}
		movieDao.deleteMoviesForCollection(collectionId);
		collectionDao.deleteMovieCollection(collectionId);
	}

	@Override
	public void shareMovieCollection(int collectionId, String shareWithUsername, boolean editable, String callingUsername) throws CollectionSharingException {
		assertCollectionEditable(collectionId, callingUsername);
		collectionDao.shareCollection(collectionId, shareWithUsername, editable);
	}

	@Override
	public void unshareMovieCollection(int collectionId, String unshareWithUsername, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = assertCollectionEditable(collectionId, callingUsername);
		if (movieCollection.getOwner().equals(unshareWithUsername)) {
			throw new CollectionSharingException("Collection owner permissions cannot be revoked.");
		}
		collectionDao.unshareCollection(collectionId, unshareWithUsername);
	}

	@Override
	public void updateEditable(int collectionId, String updateUsername, boolean editable, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = assertCollectionEditable(collectionId, callingUsername);
		if (movieCollection.getOwner().equals(updateUsername)) {
			throw new CollectionSharingException("Collection owner permissions cannot be changed.");
		}
		collectionDao.updateEditable(collectionId, updateUsername, editable);
		
	}

	@Override
	public void acceptShareOffer(int collectionId, String callingUsername) throws CollectionSharingException {
		if (!collectionDao.acceptShareOffer(collectionId, callingUsername)) {
			throw new CollectionSharingException("Unable to accept share offer for collection " + collectionId + " user " + callingUsername);
		}
	}

	@Override
	public void declineShareOffer(int collectionId, String callingUsername) throws CollectionSharingException {
		if (!collectionDao.unshareCollection(collectionId, callingUsername)) {
			throw new CollectionSharingException("Unable to decline share offer for collection " + collectionId + " user " + callingUsername);
		}
	}
	
	@Override
	public MovieCollection assertCollectionViewable(int collectionId, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null) { // collection must be viewable
			throw new CollectionSharingException("User does not have required permission to update share permission.");
		}
		return movieCollection;
	}

	@Override
	public MovieCollection assertCollectionEditable(int collectionId, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !movieCollection.isEditable()) { // collection must be editable
			throw new CollectionSharingException("User does not have required permission to update share permission.");
		}
		return movieCollection;
	}

	@Override
	public List<CollectionPermission> getCollectionPermissions(int collectionId, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !callingUsername.equals(movieCollection.getOwner())) {
			throw new CollectionSharingException("User can only view sharing permissions of collections they are the owner of.");
		}
		return collectionDao.getCollectionPermissions(collectionId);
	}

	@Override
	public CollectionPermission getCollectionPermission(int collectionId, String username, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !callingUsername.equals(movieCollection.getOwner())) {
			throw new CollectionSharingException("User can only view sharing permissions of collections they are the owner of.");
		}
		return collectionDao.getCollectionPermission(collectionId, username);
	}
}
