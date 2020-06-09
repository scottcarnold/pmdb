package org.xandercat.pmdb.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.xandercat.pmdb.dto.Movie;

/**
 * Interface for movie operations.
 * 
 * @author Scott Arnold
 */
public interface MovieDao {

	/**
	 * Delete movies of the given collection id.
	 * 
	 * @param collectionId  movie collection id
	 */
	public void deleteMoviesForCollection(String collectionId);
	
	/**
	 * Returns set of movies for the movie collection of given id.
	 * 
	 * @param collectionId movie collection id
	 * 
	 * @return set of movies for the movie collection
	 */
	public Set<Movie> getMoviesForCollection(String collectionId);
	
	/**
	 * Returns set of movies for the movie collection of given id that match the provided search string.  
	 * Search is case insensitive and can match title or any attribute value associated with the movie.
	 * 
	 * @param collectionId  movie collection id
	 * @param searchString  search string
	 * 
	 * @return movies in collection matching search string
	 */
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString);
	
	/**
	 * Returns movie of given id.
	 * 
	 * @param id  movie id
	 * 
	 * @return movie of given id
	 */
	public Optional<Movie> getMovie(String id);
	
	/**
	 * Add movie.  Collection information within the movie object should be set appropriately.
	 * 
	 * @param movie  movie to add
	 */
	public void addMovie(Movie movie);
	
	/**
	 * Add the collection of movies.  Movie collection information within the movies objects should be set appropriately.
	 * 
	 * @param movies  movies to add
	 */
	public void addMovies(Collection<Movie> movies);
	
	/**
	 * Update movie.  Movie collection information cannot be changed.
	 * 
	 * @param movie  movie to update
	 */
	public void updateMovie(Movie movie);
	
	/**
	 * Delete movie.
	 * 
	 * @param id  id of movie to delete
	 */
	public void deleteMovie(String id);
	
	/**
	 * Returns ordered list of attribute names that should be included in the movie table on the home page.
	 * 
	 * @param username  user to get table column preferences for
	 * 
	 * @return ordered list of attribute names that should be included in the movie table
	 */
	public List<String> getTableColumnPreferences(String username);
	
	/**
	 * Add attribute name to the end of list of attribute names that should be included in the movie table on the home page.
	 * 
	 * @param attributeName  attribute name to add
	 * @param username       user to add preference for
	 */
	public void addTableColumnPreference(String attributeName, String username);
	
	/**
	 * Reorder existing table column preference from given source index.  If target index is
	 * greater than source index, attribute will be moved after the given target index, and 
	 * those in between are moved up.  If target index is less than source index, attribute will
	 * be moved before the given target index, and those in between are moved down.
	 *  
	 * @param sourceIdx   index of the preference to move.
	 * @param targetIdx   target index (not necessarily the final index of the attribute)
	 * @param username    user to change preference for
	 */
	public void reorderTableColumnPreference(int sourceIdx, int targetIdx, String username);
	
	/**
	 * Remove attribute name of the given ordered index from the list of attribute names that should be included in the movie table on the home page.
	 * 
	 * @param sourceIdx  index of attribute name to remove
	 * @param username   user to delete preference for
	 */
	public void deleteTableColumnPreference(int sourceIdx, String username);
	
	/**
	 * Returns the max index of the list of attribute names that should be included in the movie table on the home page.
	 * 
	 * @param username  user to get max index for
	 * 
	 * @return max index of the list of attribute names that should be included in the movie table
	 */
	public Optional<Integer> getMaxTableColumnPreferenceIndex(String username);
	
	/**
	 * Returns list of distinct attribute names within the movie collection of given id.
	 * 
	 * @param collectionId  movie collection id
	 * 
	 * @return list of distinct attribute names for movie collection
	 */
	public List<String> getAttributeKeysForCollection(String collectionId);
	
	/**
	 * Returns set of attribute values for the given collection and attribute name.
	 * 
	 * @param collectionId   movie collection id
	 * @param attributeName  attribute name
	 * 
	 * @return set of attribute values
	 */
	public Set<String> getAttributeValuesForCollection(String collectionId, String attributeName);
	
	/**
	 * Returns list of movies within movie collection that do not have the given attribute.
	 * 
	 * @param collectionId  movie collection id
	 * @param attributeKey  attribute name
	 * 
	 * @return list of movies within movie collection that do not have the given attribute
	 */
	public List<Movie> getMoviesWithoutAttribute(String collectionId, String attributeKey);
}
