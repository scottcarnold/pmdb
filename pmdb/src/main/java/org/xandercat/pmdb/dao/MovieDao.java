package org.xandercat.pmdb.dao;

import java.util.List;

import org.xandercat.pmdb.dto.Movie;

public interface MovieDao {

	public void deleteMoviesForCollection(int collectionId);
	
	public List<Movie> getMoviesForCollection(int collectionId);
	
	public Movie getMovie(int id);
	
	public void addMovie(Movie movie);
	
	public void updateMovie(Movie movie);
	
	public void deleteMovie(int id);
	
}
