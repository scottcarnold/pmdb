package org.xandercat.pmdb.service;

import java.util.List;

import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CollectionSharingException;

public interface CollectionService {

	public MovieCollection getDefaultMovieCollection(String username);
	
	public void setDefaultMovieCollection(int collectionId, String callingUsername) throws CollectionSharingException;
	
	public MovieCollection getViewableMovieCollection(int collectionId, String callingUsername) throws CollectionSharingException;
	
	public List<MovieCollection> getViewableMovieCollections(String username);
	
	public void addMovieCollection(MovieCollection movieCollection, String callingUsername);
	
	public void updateMovieCollection(MovieCollection movieCollection, String callingUsername) throws CollectionSharingException;
	
	public void deleteMovieCollection(int collectionId, String callingUsername) throws CollectionSharingException;
	
	public void shareMovieCollection(int collectionId, String shareWithUsername, boolean editable, String callingUsername) throws CollectionSharingException;
	
	public void unshareMovieCollection(int collectionId, String unshareWithUsername, String callingUsername) throws CollectionSharingException;
	
	public void updateEditable(int collectionId, String updateUsername, boolean editable, String callingUsername) throws CollectionSharingException;
}
