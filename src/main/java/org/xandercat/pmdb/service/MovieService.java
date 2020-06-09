package org.xandercat.pmdb.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;

/**
 * Service interface for the management of movies.
 * 
 * CollectionSharingException can be thrown from most methods if action being performed requires permission on a collection the 
 * calling user does not have.
 * 
 * WebServicesExceptions can be thrown from most methods if cloud functions are required but cloud services are disabled or otherwise unavailable. 
 * 
 * @author Scott Arnold
 */
public interface MovieService {

	/**
	 * Returns all movies for the given movie collection id.  
	 *   
	 * @param collectionId      id of movie collection
	 * @param callingUsername   user making the call
	 * 
	 * @return all movies for the given movie collection
	 * @throws CollectionSharingException if user does not have permission to perform the operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public Set<Movie> getMoviesForCollection(String collectionId, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Search the movie collection of given id for the given search string.  The search string is case insensitive and can be
	 * part of the movie title or part of any attribute value associated with the movie.
	 * 
	 * @param collectionId     movie collection id
	 * @param searchString     search string
	 * @param callingUsername  user making the call
	 * 
	 * @return movies matching the provided search string
	 * @throws CollectionSharingException if user does not have permission to perform the operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Returns list of movies within the user's default/active movie collection that are not currently linked to movies on the IMDB.
	 * 
	 * @param callingUsername  user making the call
	 * 
	 * @return list of movies within default collection not linked to movies on the IMDB.
	 * @throws WebServicesException if a web service failure occurs
	 */
	public List<Movie> getUnlinkedMoviesForDefaultCollection(String callingUsername) throws WebServicesException;
	
	/**
	 * Returns movie of given id.
	 * 
	 * @param id                id of movie
	 * @param callingUsername   user making the call
	 * 
	 * @return movie of given id
	 * @throws CollectionSharingException if user does not have permission to perform the operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public Optional<Movie> getMovie(String id, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Add movie to the user's default/active movie collection.
	 * 
	 * @param movie            movie to add
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform the operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public void addMovie(Movie movie, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Update movie.
	 * 
	 * @param movie            movie to update
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform the operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public void updateMovie(Movie movie, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Delete movie.
	 * 
	 * @param id               id of movie
	 * @param callingUsername  user making the call
	 * 
	 * @throws CollectionSharingException if user does not have permission to perform the operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public void deleteMovie(String id, String callingUsername) throws CollectionSharingException, WebServicesException;
	
	/**
	 * Returns ordered list of attribute names that should be included in the movie table on the home page.
	 * 
	 * @param username  user making the call
	 * 
	 * @return ordered list of attribute names for table
	 */
	public List<String> getTableColumnPreferences(String username);
	
	/**
	 * Add attribute name to end of those attribute names that should be included in the movie table on the home page.
	 * 
	 * @param attributeName  attribute name to add to the table
	 * @param username       user making the call
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
	 * Remove attribute name from those that should be included in the movie table on the home page.  Attribute name is referenced
	 * by it's order index in the list of attribute names that are included in the table.
	 * 
	 * @param sourceIdx  index of attribute name within the list of attribute names that are included in the movie table.
	 * @param username   user making the call
	 */
	public void deleteTableColumnPreference(int sourceIdx, String username);
	
	/**
	 * Return distinct list of attribute names for the movie collection of given collection id.
	 * 
	 * @param collectionId     id of movie collection
	 * @param callingUsername  user making the call
	 * 
	 * @return list of all distinct attribute names for the movies of the given movie collection
	 * @throws CollectionSharingException if user does not have permission to perform the operation
	 * @throws WebServicesException if a web service failure occurs
	 */
	public List<String> getAttributeKeysForCollection(String collectionId, String callingUsername) throws CollectionSharingException, WebServicesException; 
	
	/**
	 * Return distinct list of IMDB ids in the user's default/active movie collection.  This represents the list of all
	 * movies within the user's default/active movie collection that are linked to movies in the IMDB.
	 * 
	 * @param callingUsername  user making the call
	 * 
	 * @return distinct list of IMDB ids in user's default/active movie collection
	 * @throws WebServicesException if a web service failure occurs
	 */
	public Set<String> getImdbIdsInDefaultCollection(String callingUsername) throws WebServicesException;
}
