package org.xandercat.pmdb.dao;

import org.xandercat.pmdb.dto.MovieCollection;

/**
 * Generic crud operations for collections needed for all storage mediums.  A standard
 * read op is not included as all of the needed read operations are already covered
 * by the share operations from @CollectionDaoShareOps.
 * @author Scott
 *
 */
public interface CollectionDaoCrudOps {
	
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
	
	/**
	 * Delete the movie collection of given id.
	 * 
	 * @param collectionId
	 */
	public void deleteMovieCollection(String collectionId);
}
