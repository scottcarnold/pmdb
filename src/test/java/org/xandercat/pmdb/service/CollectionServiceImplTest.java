package org.xandercat.pmdb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.xandercat.pmdb.dao.CollectionDao;
import org.xandercat.pmdb.dao.KeyGenerator;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dao.RandomKeyGenerator;
import org.xandercat.pmdb.dao.repository.DynamoCollectionRepository;
import org.xandercat.pmdb.dao.repository.DynamoMovieRepository;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.util.ApplicationProperties;

public class CollectionServiceImplTest {

	private static final String OTHER_NON_VIEWABLE_ID = "otherId";
	
	@Mock
	private CollectionDao collectionDao;
	
	@Mock
	private DynamoCollectionRepository dynamoCollectionRepository;
	
	@Mock
	private MovieDao movieDao;
	
	@Mock
	private DynamoMovieRepository dynamoMovieRepository;
	
	@InjectMocks
	private CollectionServiceImpl service;

	private MovieCollection movieCollection;
	
	@BeforeEach
	public void beforeEach() {
		KeyGenerator keyGenerator = new RandomKeyGenerator();
		ApplicationProperties props = new ApplicationProperties();
		props.setAwsEnabled(true);
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(service, "keyGenerator", keyGenerator);
		ReflectionTestUtils.setField(service, "applicationProperties", props);
		movieCollection = new MovieCollection();
		movieCollection.setName("Movie Collection");
		movieCollection.setId("cId");
		movieCollection.setCloud(false);
		movieCollection.setOwnerAndOwned("User", "User");
		movieCollection.setEditable(true);
		when(collectionDao.getDefaultCollectionId("User")).thenReturn(Optional.of(movieCollection.getId()));
		when(collectionDao.getViewableMovieCollection(movieCollection.getId(), "User")).thenReturn(Optional.of(movieCollection));
		when(collectionDao.getViewableMovieCollection(OTHER_NON_VIEWABLE_ID, "User")).thenReturn(Optional.empty());
	}
	
	@Test
	public void testGetDefaultMovieCollection() {
		Optional<MovieCollection> mc = service.getDefaultMovieCollection("User");
		assertTrue(mc.isPresent());
	}
	
	@Test
	public void testSetDefaultMovieCollectionWithoutAuthorization() {
		Exception exception = assertThrows(CollectionSharingException.class, () -> {
			service.setDefaultMovieCollection(OTHER_NON_VIEWABLE_ID, "User");
		});
		assertNotNull(exception);
	}
	
	@Test
	public void testGetViewableMovieCollection() throws Exception {
		MovieCollection mc = service.getViewableMovieCollection(movieCollection.getId(), "User");
		assertEquals(mc.getId(), movieCollection.getId());
	}
	
	@Test
	public void testAddMovieCollection() throws Exception {
		MovieCollection mc = new MovieCollection();
		mc.setName("New Movie Collection");
		service.addMovieCollection(mc, "User");
		verify(collectionDao, times(1)).addMovieCollection(mc);
		assertTrue(mc.isOwned());
	}
	
	@Test
	public void testUpdateMovieCollection() throws Exception {
		service.updateMovieCollection(movieCollection, "User");
		verify(collectionDao, times(1)).updateMovieCollection(movieCollection);
	}
	
	@Test
	public void testDeleteMovieCollection() throws Exception {
		service.deleteMovieCollection(movieCollection.getId(), "User");
		verify(collectionDao, times(1)).deleteMovieCollection(movieCollection.getId());
		verify(movieDao, times(1)).deleteMoviesForCollection(movieCollection.getId());
	}
	
	@Test
	public void testDeleteMovieCollectionNotAuthorized() throws Exception {
		Exception exception = assertThrows(CollectionSharingException.class, () -> {
			service.deleteMovieCollection(OTHER_NON_VIEWABLE_ID, "User");
		});
		assertNotNull(exception);		
	}
	
	@Test
	public void testUnshareMovieCollection() throws Exception {
		MovieCollection omc = new MovieCollection();
		omc.setId("otherId");
		omc.setOwnerAndOwned("User", "User");
		omc.setEditable(true);
		when(collectionDao.getViewableMovieCollection(omc.getId(), "User")).thenReturn(Optional.of(omc));
		service.unshareMovieCollection("otherId", "OtherUser", "User");
		verify(collectionDao, times(1)).unshareCollection(omc.getId(), "OtherUser");
	}
	
	@Test
	public void testUnshareOwnMovieCollectionThrowsException() throws Exception {
		MovieCollection omc = new MovieCollection();
		omc.setId("otherId");
		omc.setOwnerAndOwned("User", "User");
		omc.setEditable(true);
		when(collectionDao.getViewableMovieCollection(omc.getId(), "User")).thenReturn(Optional.of(omc));
		Exception exception = assertThrows(CollectionSharingException.class, () -> {
			service.unshareMovieCollection(omc.getId(), "User", "User");
		});
		assertNotNull(exception);			
	}
	
	@Test
	public void testUnshareMovieCollectionNotAuthorized() throws Exception {
		MovieCollection omc = new MovieCollection();
		omc.setId("otherId");
		omc.setOwnerAndOwned("User", "User");
		omc.setEditable(true);
		when(collectionDao.getViewableMovieCollection(omc.getId(), "User")).thenReturn(Optional.empty());
		Exception exception = assertThrows(CollectionSharingException.class, () -> {
			service.unshareMovieCollection(omc.getId(), "OtherUser", "User");
		});
		assertNotNull(exception);				
	}
}
