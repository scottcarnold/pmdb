package org.xandercat.pmdb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.xandercat.pmdb.dto.CollectionPermission;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.service.CollectionService;
import org.xandercat.pmdb.service.MovieService;
import org.xandercat.pmdb.service.UserService;
import org.xandercat.pmdb.util.Alerts;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CollectionControllerTest {

	@Mock
	private UserService userService;
	
	@Mock
	private CollectionService collectionService;
	
	@Mock
	private MovieService movieService;
	
	@Mock
	private Principal principal;
	
	@InjectMocks
	private CollectionController controller;
	
	private MockMvc mockMvc;
	private MovieCollection defaultMovieCollection;
	
	@BeforeEach
	public void beforeEach() {
		defaultMovieCollection = new MovieCollection();
		defaultMovieCollection.setId("1");
		defaultMovieCollection.setName("Test Collection");
		defaultMovieCollection.setOwnerAndOwned("User", "User");
		Optional<MovieCollection> optional = Optional.of(defaultMovieCollection);
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
		when(principal.getName()).thenReturn("User");
		when(collectionService.getDefaultMovieCollection(any())).thenReturn(optional);
		List<MovieCollection> movieCollections = new ArrayList<>();
		movieCollections.add(defaultMovieCollection);
		when(collectionService.getViewableMovieCollections("User")).thenReturn(movieCollections);
	}
	
	@Test
	public void testCollectionList() throws Exception {
		List<MovieCollection> shareOffers = new ArrayList<>();
		MovieCollection shareOffer = new MovieCollection();
		shareOffer.setId("id");
		shareOffer.setName("offer");
		shareOffers.add(shareOffer);
		when(collectionService.getShareOfferMovieCollections("User")).thenReturn(shareOffers);
		mockMvc.perform(get("/collections")
				.principal(principal)
		)		
				.andExpect(model().attributeExists("viewTab", "defaultMovieCollection", "movieCollections", "shareOffers"))
				.andExpect(view().name("collection/collections"));		
	}

	@Test
	public void testAddFirstCollection() throws Exception {
		when(collectionService.getViewableMovieCollections("User")).thenReturn(new ArrayList<MovieCollection>());
		mockMvc.perform(get("/collections/addCollectionSubmit")
				.principal(principal)
				.param("id", "")
				.param("name", "My New Collection")
				.param("cloud", "false")
		)		
				.andExpect(redirectedUrlPattern("/?**"));		
	}
	
	@Test
	public void testAddAnotherCollection() throws Exception {
		mockMvc.perform(get("/collections/addCollectionSubmit")
				.principal(principal)
				.param("id", "")
				.param("name", "My New Collection")
				.param("cloud", "false")
		)		
				.andExpect(model().attributeExists("viewTab", "defaultMovieCollection", "movieCollections"))
				.andExpect(view().name("collection/collections"));			
	}
	
	@Test
	public void testEditCollectionSubmit() throws Exception {
		mockMvc.perform(get("/collections/editCollectionSubmit")
				.principal(principal)
				.param("id", "")
				.param("name", "My New Collection")
				.param("cloud", "false")
		)		
				.andExpect(model().attributeExists("viewTab", "defaultMovieCollection", "movieCollections"))
				.andExpect(view().name("collection/collections"));			
	}
	
	@Test
	public void testDeleteCollection() throws Exception {
		mockMvc.perform(post("/collections/deleteCollection")
				.principal(principal)
				.param("collectionId", "abc")
		)		
				.andExpect(model().attributeExists("viewTab", "defaultMovieCollection", "movieCollections"))
				.andExpect(view().name("collection/collections"));			
	}
	
	@Test
	public void testEditSharing() throws Exception {
		MovieCollection collection = new MovieCollection();
		collection.setId("abc");
		List<CollectionPermission> permissions = new ArrayList<>();
		when(collectionService.getCollectionPermissions("abc", "User")).thenReturn(permissions);
		when(collectionService.getViewableMovieCollection("abc", "User")).thenReturn(collection);
		mockMvc.perform(get("/collections/editSharing")
				.principal(principal)
				.param("collectionId", "abc")
		)		
				.andExpect(model().attributeExists("viewTab", "movieCollection", "collectionPermissions"))
				.andExpect(view().name("collection/editSharing"));			
	}
	
	@Test
	public void testShareCollectionSubmit() throws Exception {
		PmdbUser joe = new PmdbUser("Joe");
		MovieCollection collection = new MovieCollection();
		collection.setId("abc");
		List<CollectionPermission> permissions = new ArrayList<>();
		when(userService.getUser("Joe")).thenReturn(Optional.of(joe));
		when(collectionService.getCollectionPermissions("abc", "User")).thenReturn(permissions);
		when(collectionService.getViewableMovieCollection("abc", "User")).thenReturn(collection);
		mockMvc.perform(get("/collections/shareCollectionSubmit")
				.principal(principal)
				.param("collectionId", "abc")
				.param("usernameOrEmail", "Joe")
				.param("editable", "false")
		)		
				.andExpect(model().attributeExists("viewTab", "movieCollection", "collectionPermissions"))
				.andExpect(view().name("collection/editSharing"));			
	}
	
	@Test
	public void testExport() throws Exception {
		mockMvc.perform(get("/collections/export")
				.principal(principal)
		)		
				.andExpect(model().attributeExists("exportForm", "collectionOptions", "typeOptions"))
				.andExpect(view().name("collection/export"));			
	}
	
	@Test
	public void testImportSubmit() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "originalName.xls", "application/vnd.ms-excel", "garbage".getBytes());
		mockMvc.perform(multipart("/collections/importSubmit")
				.file(file)
				.principal(principal)
		)		
				.andExpect(model().attributeExists(Alerts.KEY_ERROR_MESSAGE)) // fed it garbage, so should result in error
				.andExpect(view().name("collection/import"));			
	}
}
