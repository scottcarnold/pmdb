package org.xandercat.pmdb.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.MovieDetailsWrapper;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.util.Pair;

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
	private ApplicationService applicationService;
	
	private Builder builder(List<Pair<String>> queryParams) {
		WebTarget webTarget = restClient.target(hostUrl);
		for (Pair<String> queryParam : queryParams) {
			webTarget = webTarget.queryParam(queryParam.getFirst(), queryParam.getSecond());
		}
		return webTarget.request()
			.header(hostHeaderKey, hostHeaderValue)
			.header(apiKeyHeaderKey, apiKeyHeaderValue);
	}
	
	@Override
	public SearchResult searchImdb(String title, Integer page, String year) throws WebServicesException, ServiceLimitExceededException {
		int serviceCalls = applicationService.getImdbServiceCallCount();
		if (serviceCalls >= maxServiceCallsPerDay) {
			throw new ServiceLimitExceededException(serviceCalls);
		} else {
			applicationService.incrementImdbServiceCallCount();
		}
		if (StringUtils.isEmptyOrWhitespace(title)) {
			throw new IllegalArgumentException("Title is required.");
		}
		List<Pair<String>> queryParams = new ArrayList<Pair<String>>();
		queryParams.add(new Pair<String>("s", title.trim()));
		if (page != null) {
			queryParams.add(new Pair<String>("page", page.toString()));
		}
		if (!StringUtils.isEmptyOrWhitespace(year)) {
			queryParams.add(new Pair<String>("y", year.trim()));
		}
		queryParams.add(new Pair<String>("r", "xml"));
		try {
			return builder(queryParams).get(new GenericType<SearchResult>() {});
		} catch (Exception e) {
			throw new WebServicesException(e);
		}
	}
	
	@Override
	public MovieDetails getMovieDetails(String imdbId) throws WebServicesException, ServiceLimitExceededException {
		int serviceCalls = applicationService.getImdbServiceCallCount();
		if (serviceCalls >= maxServiceCallsPerDay) {
			throw new ServiceLimitExceededException(serviceCalls);
		} else {
			applicationService.incrementImdbServiceCallCount();
		}
		List<Pair<String>> queryParams = new ArrayList<Pair<String>>();
		queryParams.add(new Pair<String>("i", imdbId));
		queryParams.add(new Pair<String>("r", "xml"));
		MovieDetailsWrapper movieDetailsWrapper = null;
		try {
			movieDetailsWrapper = builder(queryParams).get(new GenericType<MovieDetailsWrapper>() {});
		} catch (Exception e) {
			throw new WebServicesException(e);
		}
		return movieDetailsWrapper.getMovieDetails();
	}

	private void setAttribute(Movie movie, String name, String value) {
		if (!StringUtils.isEmptyOrWhitespace(value)) {
			if (value.length() > MAX_ATTRIBUTE_VALUE_LENGTH) {
				value = value.substring(0, MAX_ATTRIBUTE_VALUE_LENGTH);
			}
			movie.addAttribute(name, value);
		}
	}
	
	@Override
	public void addImdbAttributes(Movie movie, MovieDetails movieDetails) {
		setAttribute(movie, "year", movieDetails.getYear());
		setAttribute(movie, "genre", movieDetails.getGenre());
		setAttribute(movie, "rated", movieDetails.getRated());
		setAttribute(movie, "plot", movieDetails.getPlot());
		setAttribute(movie, "actors", movieDetails.getActors());
		setAttribute(movie, "director", movieDetails.getDirector());
		setAttribute(movie, "awards", movieDetails.getAwards());
		setAttribute(movie, ImdbSearchService.IMDB_ID_KEY, movieDetails.getImdbId());
		if (!StringUtils.isEmptyOrWhitespace(movieDetails.getImdbId())) {
			setAttribute(movie, "imdb url", IMDB_URL_BASE + movieDetails.getImdbId());
		}
		setAttribute(movie, "imdb rating", movieDetails.getImdbRating());
		setAttribute(movie, "imdb votes", movieDetails.getImdbVotes());
		setAttribute(movie, "language", movieDetails.getLanguage());
		setAttribute(movie, "metascore", movieDetails.getMetascore());		
		setAttribute(movie, "poster", movieDetails.getPoster());		
		setAttribute(movie, "released", movieDetails.getReleased());
		setAttribute(movie, "runtime", movieDetails.getRuntime());
		setAttribute(movie, "type", movieDetails.getType());
		setAttribute(movie, "country", movieDetails.getCountry());
	}

	@Override
	public void removeImdbAttributes(Movie movie) {
		movie.removeAttribute("year");
		movie.removeAttribute("genre");
		movie.removeAttribute("rated");
		movie.removeAttribute("plot");
		movie.removeAttribute("actors");
		movie.removeAttribute("director");
		movie.removeAttribute("awards");
		movie.removeAttribute(ImdbSearchService.IMDB_ID_KEY);
		movie.removeAttribute("imdb url");
		movie.removeAttribute("imdb rating");
		movie.removeAttribute("imdb votes");
		movie.removeAttribute("language");
		movie.removeAttribute("metascore");		
		movie.removeAttribute("poster");		
		movie.removeAttribute("released");
		movie.removeAttribute("runtime");
		movie.removeAttribute("type");
		movie.removeAttribute("country");	
	}
}
