package org.xandercat.pmdb.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
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
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.dto.imdb.Result;
import org.xandercat.pmdb.dto.imdb.SearchResult;
import org.xandercat.pmdb.form.imdb.SearchForm;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbSearchService;
import org.xandercat.pmdb.service.MovieService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
		when(collectionService.assertCollectionEditable(any(), any())).thenReturn(defaultMovieCollection);
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

	@Test
	public void testLinkSingleMovieSearch() throws Exception {
		Movie movie = new Movie();
		movie.setTitle("title");
		movie.setId("abcdef");
		when(movieService.getMovie("abcdef", "User")).thenReturn(Optional.of(movie));
		mockMvc.perform(post("/imdbsearch/link")
				.param("movieId", "abcdef")
				.param("linkAll", "false")
				.principal(principal)
				.session(session)
		)
				.andExpect(model().attributeExists("defaultMovieCollection", "linkMovie"))
				.andExpect(model().attribute("searchForm", Matchers.hasProperty("linkImdbId", Matchers.isEmptyOrNullString())))
				.andExpect(model().attribute("searchForm", Matchers.hasProperty("linkMovieId", Matchers.equalTo("abcdef"))))
				.andExpect(model().attribute("searchForm", Matchers.hasProperty("linkAll", Matchers.equalTo(false))))
				.andExpect(view().name("imdbsearch/imdbSearch"));			
	}
	
	@Test
	public void testLinkAllMovieLink() throws Exception {
		Movie previousMovie = new Movie();
		previousMovie.setTitle("previous");
		previousMovie.setId("abcdef");
		List<Movie> movies = new ArrayList<>();
		Movie movie = new Movie();
		movie.setTitle("title");
		movie.setId("nextId");
		movies.add(movie);
		when(movieService.getUnlinkedMoviesForDefaultCollection(any())).thenReturn(movies);
		when(movieService.getMovie("abcdef", "User")).thenReturn(Optional.of(previousMovie));
		when(movieService.getMovie("nextId", "User")).thenReturn(Optional.of(movie));
		mockMvc.perform(post("/imdbsearch/searchSubmit")
				.param("title", "different")
				.param("page", "1")
				.param("linkMovieId", "abcdef")
				.param("linkImdbId", "tt123456789")
				.param("linkAll", "true")
				.principal(principal)
				.session(session)
		)
				.andExpect(model().attributeExists("defaultMovieCollection", "linkMovie"))
				.andExpect(model().attribute("searchForm", Matchers.hasProperty("linkImdbId", Matchers.isEmptyOrNullString())))
				.andExpect(model().attribute("searchForm", Matchers.hasProperty("linkMovieId", Matchers.equalTo("nextId"))))
				.andExpect(view().name("imdbsearch/imdbSearch"));			
	}
	
	@Test
	public void testLinkAllMovieLinkEnd() throws Exception {
		Movie previousMovie = new Movie();
		previousMovie.setTitle("previous");
		previousMovie.setId("abcdef");
		List<Movie> movies = new ArrayList<>();
		when(movieService.getUnlinkedMoviesForDefaultCollection(any())).thenReturn(movies);
		when(movieService.getMovie("abcdef", "User")).thenReturn(Optional.of(previousMovie));
		mockMvc.perform(post("/imdbsearch/searchSubmit")
				.param("title", "different")
				.param("page", "1")
				.param("linkMovieId", "abcdef")
				.param("linkImdbId", "tt123456789")
				.principal(principal)
				.session(session)
		)
				.andExpect(redirectedUrlPattern("/?**"));			
	}
	
	@Test
	public void testAddToCollection() throws Exception {
		MovieDetails movieDetails = new MovieDetails();
		movieDetails.setImdbId("tt123456789");
		movieDetails.setTitle("Title");
		movieDetails.setGenre("Action");
		when(imdbSearchService.getMovieDetails(any())).thenReturn(movieDetails);
		mockMvc.perform(get("/imdbsearch/addToCollection")
				.param("imdbId", "tt123456789")
				.principal(principal)
		)
				.andExpect(content().json("{'ok':true,'errorMessage':null,'content':{'imdbId':'tt123456789'}}"));
	}
}
