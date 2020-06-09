package org.xandercat.pmdb.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.SearchRequest;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.ws.ClientQueryParamMarshaller;

public class ImdbSearchServiceTest {

	@Mock
	private Client restClient;
	
	@Mock
	private WebTarget webTarget;
	
	@Mock
	private Invocation.Builder builder;
	
	@Mock
	private ApplicationService applicationService;
	
	private SearchResult searchResult;
	
	@InjectMocks
	private ImdbSearchService service;
	
	@BeforeEach
	public void beforeEach() {
		service = new ImdbRestService();
		searchResult = new SearchResult();
		MockitoAnnotations.initMocks(this);
		when(restClient.target(anyString())).thenReturn(webTarget);
		when(webTarget.queryParam(anyString(), any())).thenReturn(webTarget);
		when(webTarget.request()).thenReturn(builder);
		when(builder.header("hostHeaderKey", "hostHeaderValue")).thenReturn(builder);
		when(builder.header("apiKeyHeaderKey", "apiKeyHeaderValue")).thenReturn(builder);
		when(builder.get(any(GenericType.class))).thenReturn(searchResult);
		when(applicationService.getImdbServiceCallCount()).thenReturn(5);
		ReflectionTestUtils.setField(service,  "clientQueryParamMarshaller", new ClientQueryParamMarshaller());
		ReflectionTestUtils.setField(service, "maxServiceCallsPerDay", 10);
		ReflectionTestUtils.setField(service, "hostUrl", "http://notarealurl.org");
		ReflectionTestUtils.setField(service, "hostHeaderKey", "hostHeaderKey");
		ReflectionTestUtils.setField(service, "apiKeyHeaderKey", "apiKeyHeaderKey");
		ReflectionTestUtils.setField(service, "hostHeaderValue", "hostHeaderValue");
		ReflectionTestUtils.setField(service, "apiKeyHeaderValue", "apiKeyHeaderValue");
	}
	
	@Test
	public void testSettingMovieAttributesAddsTitle() {
		MovieDetails movieDetails = new MovieDetails();
		movieDetails.setTitle("title");
		Movie movie = new Movie();
		service.addImdbAttributes(movie, movieDetails);
		assertEquals("title", movie.getTitle());
	}

	@Test
	public void testSettingMovieAttributesPreservesTitle() {
		MovieDetails movieDetails = new MovieDetails();
		movieDetails.setTitle("title");
		Movie movie = new Movie();
		movie.setTitle("custom title");
		service.addImdbAttributes(movie, movieDetails);
		assertEquals("custom title", movie.getTitle());		
	}
	
	@Test
	public void testImdbSearch() throws Exception {
		SearchRequest request = new SearchRequest();
		request.setTitle("title");
		SearchResult result = service.searchImdb(request);
		assertNotNull(result);
	}
	
	@Test
	public void testImdbSearchServiceLimitExceeded() throws Exception {
		ReflectionTestUtils.setField(service, "maxServiceCallsPerDay", 10);
		when(applicationService.getImdbServiceCallCount()).thenReturn(15);
		SearchRequest request = new SearchRequest();
		request.setTitle("title");
		Exception exception = assertThrows(ServiceLimitExceededException.class, () -> {
			service.searchImdb(request);
		});
		assertNotNull(exception);
	}
	
	@Test
	public void testImdbSearchRequiresTitle() {
		SearchRequest request = new SearchRequest();
		request.setTitle(null);
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.searchImdb(request);
		});
		assertNotNull(exception);		
	}
}
