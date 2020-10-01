package org.xandercat.pmdb.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;

/**
 * Service interface for the management of movie collections.  Does not include methods for 
 * managing individual movies; for individual movie management, see {@link MovieService}.
 * 
 * @author Scott Arnold
 */
public interface CollectionService {

	/**
	 * Returns the user's default/active movie collection.
	 * 
	 * @param username  user to get default movie collection for
	 * 
	 * @return default movie collection for user
	 */
	public Optional<MovieCollection> getDefaultMovieCollection(String username);
	
	/**
	 * Sets the user's default/active movie collection to the movie collection of given id.
	 * 
	 * @param collectionId     movie collection id
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public void setDefaultMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Returns the viewable movie collection of given id for the given user.  The user must be able to at least view the movie
	 * or a CollectionSharingException will be thrown.
	 * 
	 * @param collectionId     id of movie collection
	 * @param callingUsername  user making the call
	 * 
	 * @return movie collection of given id
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public MovieCollection getViewableMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Returns list of movie collections that user is able to view.  This does not include movie collections where share offers are 
	 * still pending.
	 * 
	 * @param username  user
	 * 
	 * @return list of movie collection user can view
	 */
	public List<MovieCollection> getViewableMovieCollections(String username);
	
	/**
	 * Returns publicly viewable movie collection of given ID.  If the collection is not set publicly viewable or if there
	 * is an error retrieving the collection, a CollectionSharingException is thrown.
	 * 
	 * @param collectionId  id of movie collection
	 * 
	 * @return movie collection of given ID
	 * 
	 * @throws CollectionSharingException is collection is not set publicly viewable
	 */
	public MovieCollection getPublicMovieCollection(String collectionId) throws CollectionSharingException;
	
	/**
	 * Returns list of movie collections that have pending share offers for the user.
	 * 
	 * @param username  user
	 * 
	 * @return movie collections with pending share offers
	 */
	public List<MovieCollection> getShareOfferMovieCollections(String username);
	
	/**
	 * Add a new movie collection for the user.
	 * 
	 * @param movieCollection  movie collection to add
	 * @param callingUsername  user adding the movie collection
	 * @throws WebServicesException if a web service failure occurs
	 */
	public void addMovieCollection(MovieCollection movieCollection, String callingUsername) throws WebServicesException;
	
	/**
	 * Update movie collection.
	 * 
	 * @param movieCollection  movie collection to update
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public void updateMovieCollection(MovieCollection movieCollection, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Delete movie collection.  This deletes the movie collection object and all movies within that collection.  User must be the 
	 * owner of the movie collection or a CollectionSharingException will be thrown.
	 * 
	 * @param collectionId     movie collection id
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public void deleteMovieCollection(String collectionId, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Offer to share movie collection with another user.
	 * 
	 * @param collectionId       movie collection to share
	 * @param shareWithUsername  username of user to share movie collection with
	 * @param editable           whether or not the user being shared with should be able to edit the movie collection
	 * @param callingUsername    user making the share offer
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public void shareMovieCollection(String collectionId, String shareWithUsername, boolean editable, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Revoke share of movie collection with another user.
	 * 
	 * @param collectionId         movie collection to unshare
	 * @param unshareWithUsername  username of user to stop sharing with
	 * @param callingUsername      user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public void unshareMovieCollection(String collectionId, String unshareWithUsername, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Change whether or not the given user should be able to edit the movie collection that is shared with them.
	 * 
	 * @param collectionId     id of movie collection to update edit permission for
	 * @param updateUsername   name of user to change edit permission of
	 * @param editable         whether or not the updated user should be able to update the movie collection
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public void updateEditable(String collectionId, String updateUsername, boolean editable, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Accept a movie collection share offer.
	 * 
	 * @param collectionId     the movie collection id
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public void acceptShareOffer(String collectionId, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Decline a movie collection share offer.
	 * 
	 * @param collectionId     the movie collection id
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public void declineShareOffer(String collectionId, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Assert that movie collection of given id is viewable by the user.  If it is not, a CollectionSharingException is thrown.
	 * 
	 * @param collectionId     id of movie collection to check
	 * @param callingUsername  user
	 * 
	 * @return movie collection being asserted viewable
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public MovieCollection assertCollectionViewable(String collectionId, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Assert that movie collection of given id is editable by the user.  If it is not, a CollectionSharingException is thrown.
	 * 
	 * @param collectionId     id of movie collection to check
	 * @param callingUsername  user
	 * 
	 * @return movie collection being asserted editable
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public MovieCollection assertCollectionEditable(String collectionId, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Return list of collection permissions for the movie collection of given id.  
	 * 
	 * @param collectionId     id of movie collection
	 * @param callingUsername  user making the call
	 * 
	 * @return list of collection permissions on the movie collection
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public List<CollectionPermission> getCollectionPermissions(String collectionId, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Return collection permission assigned to given username for the movie collection of given id.
	 * 
	 * @param collectionId     id of movie collection to get permission on
	 * @param username         username of user to get permission on
	 * @param callingUsername  user making the call
	 * 
	 * @return collection permission for provided username over given movie collection
	 * @throws CollectionSharingException if user does not have permission to perform operation
	 */
	public Optional<CollectionPermission> getCollectionPermission(String collectionId, String username, String callingUsername) throws CollectionSharingException;
	
	/**
	 * Import a movie collection from file.
	 * 
	 * @param mFile            file with table of movies to be imported
	 * @param collectionName   name for imported movie collection
	 * @param cloud            whether or not movie collection should be stored on the cloud
	 * @param sheetNames       sheet names to import
	 * @param columnNames      column names to import
	 * @param callingUsername  user making the call
	 * 
	 * @throws IOException if IO error occurs
	 */
	public void importCollection(MultipartFile mFile, String collectionName, boolean cloud, List<String> sheetNames, List<String> columnNames, String callingUsername) throws IOException;
}
