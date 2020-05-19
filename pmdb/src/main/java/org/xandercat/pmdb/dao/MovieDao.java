package org.xandercat.pmdb.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.xandercat.pmdb.dto.Movie;

public interface MovieDao {

	public void deleteMoviesForCollection(String collectionId);
	
	public Set<Movie> getMoviesForCollection(String collectionId);
	
	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString);
	
	public Movie getMovie(String id);
	
	public void addMovie(Movie movie);
	
	public void addMovies(Collection<Movie> movies);
	
	public void updateMovie(Movie movie);
	
	public void deleteMovie(String id);
	
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
	 */
	public void reorderTableColumnPreference(int sourceIdx, int targetIdx, String username);
	
	public void deleteTableColumnPreference(int sourceIdx, String username);
	
	public Integer getMaxTableColumnPreferenceIndex(String username);
	
	public List<String> getAttributeKeysForCollection(String collectionId);
	
	public Set<String> getAttributeValuesForCollection(String collectionId, String attributeName);
}
