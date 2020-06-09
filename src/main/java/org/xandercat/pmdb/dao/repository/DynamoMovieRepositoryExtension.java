package org.xandercat.pmdb.dao.repository;

import java.util.List;
import java.util.Set;

import org.xandercat.pmdb.dto.Movie;

/**
 * Interface for additional functions beyond basic CRUD functions for movies.
 * 
 * @author Scott Arnold
 */
public interface DynamoMovieRepositoryExtension {

	/**
	 * Search for movies of the given collection ID using the provided search string.  Search
	 * string should be handled as case insensitive.  Results should be returned for movies
	 * where either the title or any of the movie's attribute values matches the provided search string.
	 * 
	 * @param collectionId  id of movie collection to search
	 * @param searchString  search string
	 * 
	 * @return movies matching the provided search string
	 */
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString);
	
	/**
	 * Return list of all distinct attribute keys for movies within the movie collection of given id.
	 * 
	 * @param collectionId  id of movie collection
	 * 
	 * @return distinct list of attribute keys for movies within the movie collection
	 */
	public List<String> getAttributeKeysForCollection(String collectionId);
	
	/**
	 * Return set of all distinct attribute values for a given attribute name within a given collection.
	 * 
	 * @param collectionId   id of movie collection
	 * @param attributeName  name of attribute to get distinct values for
	 * 
	 * @return set of all distinct attribute values for a given attribute name within a given collection
	 */
	public Set<String> getAttributeValuesForCollection(String collectionId, String attributeName);
	
	/**
	 * Return list of all movies within a given collection that do not have the given attribute.
	 * 
	 * @param collectionId  id of movie collection
	 * @param attributeKey  name of attribute
	 * 
	 * @return list of all movies within movie collection that do not contain the attribute specified
	 */
	public List<Movie> getMoviesWithoutAttribute(String collectionId, String attributeKey);
}
