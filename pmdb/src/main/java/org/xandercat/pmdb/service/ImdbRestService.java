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
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.MovieDetailsWrapper;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.util.Pair;

@Component
public class ImdbRestService {
	
	private static final Logger LOGGER = LogManager.getLogger(ImdbRestService.class);
	
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
	
	@Autowired
	private Client restClient;
	
	private Builder builder(List<Pair<String>> queryParams) {
		WebTarget webTarget = restClient.target(hostUrl);
		for (Pair<String> queryParam : queryParams) {
			webTarget = webTarget.queryParam(queryParam.getFirst(), queryParam.getSecond());
		}
		return webTarget.request()
			.header(hostHeaderKey, hostHeaderValue)
			.header(apiKeyHeaderKey, apiKeyHeaderValue);
	}
	
	public SearchResult searchImdb(String title, Integer page, String year) {
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
		SearchResult searchResult = builder(queryParams).get(new GenericType<SearchResult>() {});
		return searchResult;
	}
	
	public MovieDetails getMovieDetails(String imdbId) {
		List<Pair<String>> queryParams = new ArrayList<Pair<String>>();
		queryParams.add(new Pair<String>("i", imdbId));
		queryParams.add(new Pair<String>("r", "xml"));
		MovieDetailsWrapper movieDetailsWrapper = builder(queryParams).get(new GenericType<MovieDetailsWrapper>() {});
		return movieDetailsWrapper.getMovieDetails();
	}
}
