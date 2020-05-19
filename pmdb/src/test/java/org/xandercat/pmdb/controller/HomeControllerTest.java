package org.xandercat.pmdb.controller;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.MovieService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;

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
	
	@InjectMocks
	private HomeController homeController;
	
	@BeforeEach
	public void setup() {
		defaultMovieCollection = new MovieCollection();
		defaultMovieCollection.setId("1");
		defaultMovieCollection.setName("Test Collection");
		defaultMovieCollection.setOwner("User", "User");
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
		when(principal.getName()).thenReturn("User");
		when(collectionService.getDefaultMovieCollection(any())).thenReturn(defaultMovieCollection);
		try {
			when(movieService.getMoviesForCollection(any(), any())).thenReturn(new HashSet<Movie>());
		} catch (CollectionSharingException e) {
		}
	}
	
	@Test
	public void HomeControllerTestTestingSetup() throws Exception {
		mockMvc.perform(get("/")
				.flashAttr("attributeNames", new ArrayList<String>())
				.principal(principal)
		)		
				.andExpect(view().name("movie/movies"));
	}

}
