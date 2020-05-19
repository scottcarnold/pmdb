package org.xandercat.pmdb.dao;

import java.util.List;

import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;

/**
 * Sharing operations related to collections.
 * 
 * @author Scott Arnold
 */
public interface CollectionDaoShareOps {
	
	/**
	 * Returns list of movie collections user has authority to view (or edit).
	 * Does not include movie collections where share offer has not been accepted.
	 * 
	 * @param username username
	 * @return list of movie collections user can view
	 */
	public List<MovieCollection> getViewableMovieCollections(String username);
	
	/**
	 * Returns list of movie collections that other users have offered to share
	 * with the user where share offer has not been accepted.
	 * 
	 * @param username username
	 * @return list of movie collections with pending share offers
	 */
	public List<MovieCollection> getShareOfferMovieCollections(String username);
	
	/**
	 * Returns the movie collection for the given collectionId.  Editable flag will be 
	 * set based on provided username.  If username does not have valid permission for
	 * collection, null is returned.
	 * 
	 * @param collectionId
	 * @param username
	 * 
	 * @return viewable movie collection of given id (or null if collection is not viewable)
	 */
	public MovieCollection getViewableMovieCollection(String collectionId, String username);

	/**
	 * Offer to share a collection of given id to the given username.
	 * 
	 * @param collectionId  id of movie collection
	 * @param username      username of user to share collection with
	 * @param editable      whether or not user should be able to edit the collection
	 */
	public void shareCollection(String collectionId, String username, boolean editable);
	
	/**
	 * Revokes permission of given user to be able to view or edit the given collection.
	 * 
	 * @param collectionId  id of movie collection
	 * @param username      username of user to stop sharing collection with
	 * @return whether or not permission was removed
	 */
	public boolean unshareCollection(String collectionId, String username);
	
	/**
	 * Returns the id of the given users default movie collection, or null
	 * if no default movie collection has been set.
	 * 
	 * @param username      username
	 * @return              id of default movie collection
	 */
	public String getDefaultCollectionId(String username);
	
	/**
	 * Sets the default collection of the user of given username to the movie collection of given id.
	 * 
	 * @param username      username
	 * @param collectionId  collection to set as default
	 */
	public void setDefaultCollection(String username, String collectionId);
	
	/**
	 * Accept share offer for the movie collection of given id for the user of given username.
	 * 
	 * @param collectionId  id of movie collection
	 * @param username      username
	 * @return whether or not the collection permission was updated
	 */
	public boolean acceptShareOffer(String collectionId, String username);
	
	/**
	 * Return list of collection permissions for the given collection excluding permission for the owner.
	 * The owner does not need to see their own permission entry.
	 * 
	 * @param collectionId
	 * @return list of collection permissions for the collection
	 */
	public List<CollectionPermission> getCollectionPermissions(String collectionId);
	
	/**
	 * Returns the collection permission for the user of given username for the collection of given id.
	 * 
	 * @param collectionId  id of movie collection
	 * @param username      username
	 * @return collection permission for user
	 */
	public CollectionPermission getCollectionPermission(String collectionId, String username);
	
	/**
	 * Sets whether or not the given user can edit the given collection.
	 * 
	 * @param collectionId  id of movie collection
	 * @param username      username
	 * @param editable      whether or not collection should be editable by the user
	 */
	public void updateEditable(String collectionId, String username, boolean editable);
}
