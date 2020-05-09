package org.xandercat.pmdb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.CollectionDao;
import org.xandercat.pmdb.dao.MovieDao;
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
		MovieCollection viewableMovieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (viewableMovieCollection == null) { // collection must be viewable
			throw new CollectionSharingException("User does not have required permission to view collection.");
		}		
		collectionDao.setDefaultCollection(callingUsername, collectionId);
		
	}

	@Override
	public List<MovieCollection> getViewableMovieCollections(String username) {
		return collectionDao.getViewableMovieCollections(username);
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
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !movieCollection.isEditable()) { // collection must be editable
			throw new CollectionSharingException("User does not have required permission to share collection.");
		}
		collectionDao.shareCollection(collectionId, shareWithUsername, editable);
	}

	@Override
	public void unshareMovieCollection(int collectionId, String unshareWithUsername, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !movieCollection.isEditable()) { // collection must be editable
			throw new CollectionSharingException("User does not have required permission to unshare collection.");
		}
		collectionDao.unshareCollection(collectionId, unshareWithUsername);
	}

	@Override
	public void updateEditable(int collectionId, String updateUsername, boolean editable, String callingUsername) throws CollectionSharingException {
		MovieCollection movieCollection = collectionDao.getViewableMovieCollection(collectionId, callingUsername);
		if (movieCollection == null || !movieCollection.isEditable()) { // collection must be editable
			throw new CollectionSharingException("User does not have required permission to update share permission.");
		}	
		collectionDao.updateEditable(collectionId, updateUsername, editable);
	}
}
