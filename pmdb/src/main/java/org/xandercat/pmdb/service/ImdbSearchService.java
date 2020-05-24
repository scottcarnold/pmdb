package org.xandercat.pmdb.service;

import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.exception.WebServicesException;

public interface ImdbSearchService {

	public static final String IMDB_ID_KEY = "Imdb Id";
	
	public SearchResult searchImdb(String title, Integer page, String year) throws WebServicesException, ServiceLimitExceededException;
	
	public MovieDetails getMovieDetails(String imdbId) throws WebServicesException, ServiceLimitExceededException;
	
	public void addImdbAttributes(Movie movie, MovieDetails movieDetails);
	
	public void removeImdbAttributes(Movie movie);
}
