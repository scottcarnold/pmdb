package org.xandercat.pmdb.controller;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.ImdbAttribute;
import org.xandercat.pmdb.service.MovieService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

public class HomeControllerTest {

	private MockMvc mockMvc;
	private MovieCollection defaultMovieCollection;
	
	@Mock
	private MovieService movieService;
	
	@Mock
	private CollectionService collectionService;
	
	@Mock
	private Principal principal;
	
	@Mock
	private MockHttpSession session;
	
	@InjectMocks
	private HomeController homeController;
	
	@BeforeEach
	public void setup() {
		defaultMovieCollection = new MovieCollection();
		defaultMovieCollection.setId("1");
		defaultMovieCollection.setName("Test Collection");
		defaultMovieCollection.setOwnerAndOwned("User", "User");
		Optional<MovieCollection> optional = Optional.of(defaultMovieCollection);
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
		when(principal.getName()).thenReturn("User");
		when(collectionService.getDefaultMovieCollection(any())).thenReturn(optional);
		try {
			when(movieService.getMoviesForCollection(any(), any())).thenReturn(new HashSet<Movie>());
		} catch (CollectionSharingException | WebServicesException e) {
		}
	}
	
	@Test
	public void testHomePage() throws Exception {
		mockMvc.perform(get("/")
				.flashAttr("attributeNames", new ArrayList<String>())
				.principal(principal)
		)		
				.andExpect(model().attributeExists("viewTab", "attributeNames", "searchForm", "defaultMovieCollection", "movies", "attrColumns", "unlinkCount"))
				.andExpect(view().name("movie/movies"));
	}
	
	@Test
	public void testHomePageRedirectToCollections() throws Exception {
		when(collectionService.getDefaultMovieCollection(any())).thenReturn(Optional.empty());
		mockMvc.perform(get("/")
				.flashAttr("attributeNames", new ArrayList<String>())
				.principal(principal)
		)
				.andExpect(redirectedUrlPattern("/collections?**"));
	}

	@Test
	public void testMovieStatistics() throws Exception {
		Movie movie = new Movie();
		movie.setId("id");
		movie.setTitle("title");
		movie.addAttribute(ImdbAttribute.IMDB_VOTES.getKey(), "1,200");
		movie.addAttribute(ImdbAttribute.GENRE.getKey(), "Action, Drama");
		Set<Movie> movies = new HashSet<Movie>();
		movies.add(movie);
		when(movieService.getMovie("id", "User")).thenReturn(Optional.of(movie));
		when(movieService.getMoviesForCollection(defaultMovieCollection.getId(), "User")).thenReturn(movies);
		mockMvc.perform(get("/movies/movieStatistics")
				.principal(principal)
				.param("movieId", "id")
		)
				.andExpect(model().attributeExists("movie", "genreStatistics", "genresMostCommon", "genresLeastCommon"))
				.andExpect(view().name("movie/movieStatistics"));
	}
	
	@Test
	public void testConfigureColumns() throws Exception {
		when(movieService.getTableColumnPreferences("User")).thenReturn(Collections.singletonList(ImdbAttribute.RATED.getKey()));
		mockMvc.perform(get("/movies/configureColumns")
				.principal(principal)
				.session(session)
		)
				.andExpect(model().attributeExists("tableColumnPreferences", "tableColumnOptions"))
				.andExpect(view().name("movie/configureColumns"));
	}
}
