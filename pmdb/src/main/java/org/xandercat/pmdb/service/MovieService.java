package org.xandercat.pmdb.service;

import java.util.List;
import java.util.Set;

import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.exception.PmdbException;

/**
 * Service interface for the management of movies.
 * 
 * @author Scott Arnold
 */
public interface MovieService {

	public Set<Movie> getMoviesForCollection(String collectionId, String callingUsername) throws CollectionSharingException;
	
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString, String callingUsername) throws CollectionSharingException;
	
	public Movie getMovie(String id, String callingUsername) throws CollectionSharingException;
	
	public void addMovie(Movie movie, String callingUsername) throws CollectionSharingException;
	
	public void updateMovie(Movie movie, String callingUsername) throws CollectionSharingException;
	
	public void deleteMovie(String id, String callingUsername) throws CollectionSharingException;
	
	public List<String> getTableColumnPreferences(String username);
	
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
	 * 
	 * @throws PmdbException if sourceIdx or targetIdx are out of range
	 */
	public void reorderTableColumnPreference(int sourceIdx, int targetIdx, String username) throws PmdbException;
	
	public void deleteTableColumnPreference(int sourceIdx, String username);
	
	public List<String> getAttributeKeysForCollection(String collectionId, String callingUsername) throws CollectionSharingException; 
	
	public Set<String> getImdbIdsInDefaultCollection(String callingUsername);
}
