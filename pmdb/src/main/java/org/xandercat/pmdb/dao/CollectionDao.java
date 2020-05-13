package org.xandercat.pmdb.dao;

import java.util.List;

import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;

/**
 * Interface for movie collection functions.  While these methods should take care of sharing updates, 
 * actual enforcement of sharing rules should be implemented by the calling service.
 * 
 * @author Scott Arnold
 */
public interface CollectionDao {

	public List<MovieCollection> getViewableMovieCollections(String username);
	
	public List<MovieCollection> getShareOfferMovieCollections(String username);
	/**
	 * Returns the movie collection for the given collectionId.  Editable flag will be 
	 * set based on provided username.  If username does not have valid permission for
	 * collection, null is returned.
	 * 
	 * @param collectionId
	 * @param username
	 * 
	 * @return
	 */
	public MovieCollection getViewableMovieCollection(int collectionId, String username);
	
	/**
	 * Add a new movie collection.  Editable property is ignored.  Owner is set based on
	 * value provided in the movie collection.  Id will be set within this method.
	 * 
	 * @param movieCollection
	 */
	public void addMovieCollection(MovieCollection movieCollection);
	
	/**
	 * Update the provided movie collection. Owner and editable properties are ignored.
	 * 
	 * @param movieCollection
	 */
	public void updateMovieCollection(MovieCollection movieCollection);
	
	public void deleteMovieCollection(int collectionId);
	
	public void shareCollection(int collectionId, String username, boolean editable);
	
	public void updateEditable(int collectionId, String username, boolean editable);
	
	public boolean unshareCollection(int collectionId, String username);
	
	public Integer getDefaultCollectionId(String username);
	
	public void setDefaultCollection(String username, int collectionId);
	
	public boolean acceptShareOffer(int collectionId, String username);
	
	/**
	 * Return list of collection permissions for the given collection excluding permission for the owner.
	 * The owner does not need to see their own permission entry.
	 * 
	 * @param collectionId
	 * @return
	 */
	public List<CollectionPermission> getCollectionPermissions(int collectionId);
	
	public CollectionPermission getCollectionPermission(int collectionId, String username);
}
