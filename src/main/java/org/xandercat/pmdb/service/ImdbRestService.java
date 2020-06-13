package org.xandercat.pmdb.service;

import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.imdb.MovieDetailsRequest;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.MovieDetailsWrapper;
import org.xandercat.pmdb.dto.imdb.ResponseType;
import org.xandercat.pmdb.dto.imdb.SearchRequest;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.util.format.FormatUtil;
import org.xandercat.pmdb.ws.ClientQueryParamMarshaller;

@Component
public class ImdbRestService implements ImdbSearchService {
	
	private static final Logger LOGGER = LogManager.getLogger(ImdbRestService.class);
	
	private static final int MAX_ATTRIBUTE_VALUE_LENGTH = 400;
	private static final String IMDB_URL_BASE = "https://www.imdb.com/title/";
	
	@Value("${imdb.rapidapi.host.url}")
	private String hostUrl;
	
	@Value("${imdb.rapidapi.host.header.key}")
	private String hostHeaderKey;
	
	@Value("${imdb.rapidapi.host.header.value}")
	private String hostHeaderValue;
	
	@Value("${imdb.rapidapi.key.header.key}")
	private String apiKeyHeaderKey;
	
	@Value("${imdb.rapidapi.key.header.value}")
	private String apiKeyHeaderValue;
	
	@Value("${imdb.calls.per.day.maximum}")
	private int maxServiceCallsPerDay;
	
	@Autowired
	private Client restClient;
	
	@Autowired
	private ClientQueryParamMarshaller clientQueryParamMarshaller;
	
	@Autowired
	private ApplicationService applicationService;

	private Builder builder(Object request) {
		WebTarget webTarget = restClient.target(hostUrl);
		webTarget = clientQueryParamMarshaller.queryParams(webTarget, request);
		return webTarget.request()
			.header(hostHeaderKey, hostHeaderValue)
			.header(apiKeyHeaderKey, apiKeyHeaderValue);
	}
	
	public SearchResult searchImdb(SearchRequest request) throws WebServicesException, ServiceLimitExceededException {
		int serviceCalls = applicationService.getImdbServiceCallCount();
		if (serviceCalls >= maxServiceCallsPerDay) {
			throw new ServiceLimitExceededException(serviceCalls, maxServiceCallsPerDay);
		} else {
			applicationService.incrementImdbServiceCallCount();
		}
		if (FormatUtil.isBlank(request.getTitle())) {
			throw new IllegalArgumentException("Title is required.");
		}
		request.setResponseType(ResponseType.XML);
		request.setTitle(request.getTitle().replaceAll("[\\u0091\\u0092]", "'")); // for clients that insist on using curly quotes that would cause search to fail
		try {
			return builder(request).get(new GenericType<SearchResult>() {});
		} catch (Exception e) {
			throw new WebServicesException(e);
		}
	}
	
	@Override
	public MovieDetails getMovieDetails(MovieDetailsRequest request) throws WebServicesException, ServiceLimitExceededException {
		int serviceCalls = applicationService.getImdbServiceCallCount();
		if (serviceCalls >= maxServiceCallsPerDay) {
			throw new ServiceLimitExceededException(serviceCalls, maxServiceCallsPerDay);
		} else {
			applicationService.incrementImdbServiceCallCount();
		}
		request.setResponseType(ResponseType.XML);
		MovieDetailsWrapper movieDetailsWrapper = null;
		try {
			movieDetailsWrapper = builder(request).get(new GenericType<MovieDetailsWrapper>() {});
		} catch (Exception e) {
			throw new WebServicesException(e);
		}
		return movieDetailsWrapper.getMovieDetails();
	}

	private void setAttribute(Movie movie, ImdbAttribute imdbAttribute, String value) {
		if (FormatUtil.isNotBlank(value)) {
			if (value.length() > MAX_ATTRIBUTE_VALUE_LENGTH) {
				value = value.substring(0, MAX_ATTRIBUTE_VALUE_LENGTH);
			}
			movie.addAttribute(imdbAttribute.getKey(), value);
		}
	}
	
	@Override
	public void addImdbAttributes(Movie movie, MovieDetails movieDetails) {
		if (FormatUtil.isBlank(movie.getTitle())) {
			// only add title if it doesn't already have one; this allows custom names for pre-existing movies
			movie.setTitle(movieDetails.getTitle());
		}
		setAttribute(movie, ImdbAttribute.YEAR, movieDetails.getYear());
		setAttribute(movie, ImdbAttribute.GENRE, movieDetails.getGenre());
		setAttribute(movie, ImdbAttribute.RATED, movieDetails.getRated());
		setAttribute(movie, ImdbAttribute.PLOT, movieDetails.getPlot());
		setAttribute(movie, ImdbAttribute.ACTORS, movieDetails.getActors());
		setAttribute(movie, ImdbAttribute.DIRECTOR, movieDetails.getDirector());
		setAttribute(movie, ImdbAttribute.AWARDS, movieDetails.getAwards());
		setAttribute(movie, ImdbAttribute.IMDB_ID, movieDetails.getImdbId());
		if (FormatUtil.isNotBlank(movieDetails.getImdbId())) {
			setAttribute(movie, ImdbAttribute.IMDB_URL, IMDB_URL_BASE + movieDetails.getImdbId());
		}
		setAttribute(movie, ImdbAttribute.IMDB_RATING, movieDetails.getImdbRating());
		setAttribute(movie, ImdbAttribute.IMDB_VOTES, movieDetails.getImdbVotes());
		setAttribute(movie, ImdbAttribute.LANGUAGE, movieDetails.getLanguage());
		setAttribute(movie, ImdbAttribute.METASCORE, movieDetails.getMetascore());		
		setAttribute(movie, ImdbAttribute.POSTER, movieDetails.getPoster());		
		setAttribute(movie, ImdbAttribute.RELEASED, movieDetails.getReleased());
		setAttribute(movie, ImdbAttribute.RUNTIME, movieDetails.getRuntime());
		setAttribute(movie, ImdbAttribute.TYPE, movieDetails.getType());
		setAttribute(movie, ImdbAttribute.COUNTRY, movieDetails.getCountry());
	}

	@Override
	public void removeImdbAttributes(Movie movie) {
		Arrays.stream(ImdbAttribute.values()).forEach(imdbAttribute -> movie.removeAttribute(imdbAttribute.getKey()));	
	}
}
