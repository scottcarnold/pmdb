package org.xandercat.pmdb.service;

import java.util.List;

import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.exception.CollectionSharingException;

public interface MovieService {

	public List<Movie> getMoviesForCollection(int collectionId, String callingUsername) throws CollectionSharingException;
	
	public Movie getMovie(int id, String callingUsername) throws CollectionSharingException;
	
	public void addMovie(Movie movie, String callingUsername) throws CollectionSharingException;
	
	public void updateMovie(Movie movie, String callingUsername) throws CollectionSharingException;
	
	public void deleteMovie(int id, String callingUsername) throws CollectionSharingException;
}
