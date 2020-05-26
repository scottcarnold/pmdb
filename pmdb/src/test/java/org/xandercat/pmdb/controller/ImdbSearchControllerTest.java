package org.xandercat.pmdb.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.imdb.Result;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.form.imdb.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbSearchService;
import org.xandercat.pmdb.service.MovieService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ImdbSearchControllerTest {

	@Mock
	private ImdbSearchService imdbSearchService;
	
	@Mock
	private MovieService movieService;
	
	@Mock
	private CollectionService collectionService;

	@Mock
	private Principal principal;
	
	@Mock
	private MockHttpSession session;
	
	@InjectMocks
	private ImdbSearchController imdbSearchController;
	
	private MockMvc mockMvc;
	private MovieCollection defaultMovieCollection;
	private SearchForm searchForm;
	private SearchResult searchResult;
	
	@BeforeEach
	public void beforeEach() throws Exception {
		defaultMovieCollection = new MovieCollection();
		defaultMovieCollection.setId("1");
		defaultMovieCollection.setName("Test Collection");
		defaultMovieCollection.setOwnerAndOwned("User", "User");
		searchForm = new SearchForm();
		searchForm.setTitle("title");
		searchResult = new SearchResult();
		searchResult.setResults(new ArrayList<Result>());
		searchResult.setTotalResults("0");
		Optional<MovieCollection> optional = Optional.of(defaultMovieCollection);
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(imdbSearchController).build();
		when(principal.getName()).thenReturn("User");
		when(collectionService.getDefaultMovieCollection(any())).thenReturn(optional);
		when(imdbSearchService.searchImdb(any())).thenReturn(searchResult);
	}
	
	@Test
	public void testNoCollectionSearch() throws Exception {
		when(collectionService.getDefaultMovieCollection(any())).thenReturn(Optional.empty());
		mockMvc.perform(post("/imdbsearch/searchSubmit")
				.param("title", "different")
				.param("page", "1")
				.principal(principal)
				.session(session)
		)
				.andExpect(model().attributeExists("searchForm"))
				.andExpect(model().attribute("searchForm", Matchers.hasProperty("title", Matchers.equalTo("different"))))
				.andExpect(view().name("imdbsearch/imdbSearch"));
	}
	
	@Test
	public void testAddMovieSearch() throws Exception {
		mockMvc.perform(post("/imdbsearch/searchSubmit")
				.param("title", "different")
				.param("page", "1")
				.principal(principal)
				.session(session)
		)
				.andExpect(model().attributeExists("defaultMovieCollection"))
				.andExpect(view().name("imdbsearch/imdbSearch"));		
	}

	public void testLinkSingleMovieLink() {
		
	}
	
	public void testLinkAllMovieLink() {
		
	}
	
	public void testLinkAllMovieLinkEnd() {
		
	}
}
