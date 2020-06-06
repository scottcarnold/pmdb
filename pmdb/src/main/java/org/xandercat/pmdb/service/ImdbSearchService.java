package org.xandercat.pmdb.service;

import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.imdb.MovieDetailsRequest;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.SearchRequest;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.exception.WebServicesException;

/**
 * Interface for IMDB search operations.
 * 
 * @author Scott Arnold
 */
public interface ImdbSearchService {

	public static final String IMDB_ID_KEY = "Imdb Id";
	
	/**
	 * Search the IMDB for movies given the provided search request.
	 * 
	 * @param request  search request for movies
	 * 
	 * @return result of search
	 * @throws WebServicesException if a web service failure occurs
	 * @throws ServiceLimitExceededException if there are too many requests to the IMDB search service for the day
	 */
	public SearchResult searchImdb(SearchRequest request) throws WebServicesException, ServiceLimitExceededException;
	
	/**
	 * Retrieve list of movie details for the movie specified by the provided request.
	 * 
	 * @param request  movie details request
	 * 
	 * @return movie details for movie
	 * @throws WebServicesException if a web service failure occurs
	 * @throws ServiceLimitExceededException if there are too many requests to the IMDB search service for the day
	 */
	public MovieDetails getMovieDetails(MovieDetailsRequest request) throws WebServicesException, ServiceLimitExceededException;
	
	/**
	 * Adds all movie details to the provided movie.  Movie title will only be set from the movie details if the 
	 * movie does not already have a movie title.  Any other attributes will always overwrite any previously 
	 * existing attributes.
	 * 
	 * @param movie         movie to add movie details to
	 * @param movieDetails  movie details to add to movie
	 */
	public void addImdbAttributes(Movie movie, MovieDetails movieDetails);
	
	/**
	 * Removes all IMDB sourced movie details from the given movie.
	 * 
	 * @param movie  movie to remove IMDB attributes from
	 */
	public void removeImdbAttributes(Movie movie);
}
