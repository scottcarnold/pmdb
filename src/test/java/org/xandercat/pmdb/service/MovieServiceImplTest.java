package org.xandercat.pmdb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.xandercat.pmdb.dao.KeyGenerator;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dao.RandomKeyGenerator;
import org.xandercat.pmdb.dao.repository.DynamoMovieRepository;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;
import org.xandercat.pmdb.util.ApplicationProperties;

public class MovieServiceImplTest {

	@Mock
	private MovieDao movieDao;
	
	@Mock
	private DynamoMovieRepository dynamoMovieRepository;
	
	@Mock
	private CollectionService collectionService;
	
	@InjectMocks
	private MovieServiceImpl service;
	
	private MovieCollection movieCollection;
	private Movie movie;
	
	@BeforeEach
	public void beforeEach() {
		KeyGenerator keyGenerator = new RandomKeyGenerator();
		ApplicationProperties props = new ApplicationProperties();
		props.setAwsEnabled(true);
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(service, "keyGenerator", keyGenerator);
		ReflectionTestUtils.setField(service, "applicationProperties", props);
		movieCollection = new MovieCollection();
		movieCollection.setId("cId");
		movieCollection.setEditable(true);
		movieCollection.setName("Name");
		movieCollection.setOwnerAndOwned("User", "User");
		movieCollection.setCloud(false);
		Set<Movie> movies = new HashSet<Movie>();
		movie = new Movie();
		movie.setTitle("Title");
		movie.setId("id");
		movie.setCollectionId(movieCollection.getId());
		movies.add(movie);
		when(movieDao.getMoviesForCollection(movieCollection.getId())).thenReturn(movies);
		when(movieDao.getMovie(movie.getId())).thenReturn(Optional.of(movie));
	}
	
	@Test
	public void testGetMoviesForCollection() throws Exception {
		when(collectionService.assertCollectionViewable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		service.getMoviesForCollection(movieCollection.getId(), "User");
		verify(movieDao, times(1)).getMoviesForCollection(movieCollection.getId());
	}
	
	@Test
	public void testSearchMoviesForCollection() throws Exception {
		when(collectionService.assertCollectionViewable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		service.searchMoviesForCollection(movieCollection.getId(), "Title", "User");
		verify(movieDao, times(1)).searchMoviesForCollection(movieCollection.getId(), "Title");		
	}
	
	@Test
	public void testGetMovie() throws Exception {
		when(collectionService.assertCollectionViewable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		Optional<Movie> m = service.getMovie(movie.getId(), "User");
		assertTrue(m.isPresent());
	}
	
	@Test
	public void testGetMovieFromCloud() throws Exception {
		movieCollection.setCloud(true);
		when(collectionService.assertCollectionViewable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		when(movieDao.getMovie(movie.getId())).thenReturn(Optional.empty());
		when(dynamoMovieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
		Optional<Movie> m = service.getMovie(movie.getId(), "User");
		assertTrue(m.isPresent());		
	}
	
	@Test
	public void testAddMovie() throws Exception {
		movieCollection.setCloud(true);
		movie.setId(null);
		when(collectionService.assertCollectionEditable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		service.addMovie(movie, "User");
		verify(dynamoMovieRepository, times(1)).save(movie);
		assertNotNull(movie.getId());
	}
	
	@Test
	public void testUpdateMovie() throws Exception {
		movieCollection.setCloud(true);
		when(collectionService.assertCollectionEditable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		service.updateMovie(movie, "User");
		verify(dynamoMovieRepository, times(1)).save(movie);
	}
	
	@Test
	public void testDeleteMovie() throws Exception {
		movieCollection.setCloud(true);
		when(movieDao.getMovie(movie.getId())).thenReturn(Optional.empty());
		when(dynamoMovieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
		when(collectionService.assertCollectionEditable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		service.deleteMovie(movie.getId(), "User");
		verify(dynamoMovieRepository, times(1)).delete(movie);
	}
	
	@Test
	public void testReorderTableColumns() throws Exception {
		when(movieDao.getMaxTableColumnPreferenceIndex("User")).thenReturn(Optional.of(Integer.valueOf(1)));
		service.reorderTableColumnPreference(0, 1, "User");
		verify(movieDao, times(1)).reorderTableColumnPreference(0, 1, "User");
	}
	
	@Test
	public void testReorderTableColumnsBadIndex() throws Exception {
		when(movieDao.getMaxTableColumnPreferenceIndex("User")).thenReturn(Optional.of(Integer.valueOf(1)));
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			service.reorderTableColumnPreference(0, 2, "User");
		});
		assertNotNull(exception);
	}
	
	@Test
	public void testGetAttributeKeysForCollection() throws Exception {
		when(collectionService.assertCollectionViewable(movieCollection.getId(), "User")).thenReturn(movieCollection);
		List<String> attrList = new ArrayList<String>();
		attrList.add("Rating");
		attrList.add("Genre");
		when(movieDao.getAttributeKeysForCollection(movieCollection.getId())).thenReturn(attrList);
		List<String> attrKeys = service.getAttributeKeysForCollection(movieCollection.getId(), "User");
		assertNotNull(attrKeys);
		assertEquals(2, attrKeys.size());
		assertEquals("Genre", attrKeys.get(0));  // service method should return them sorted
	}
	
	@Test
	public void testGetImdbIdsInDefaultCollection() throws Exception {
		when(collectionService.getDefaultMovieCollection("User")).thenReturn(Optional.of(movieCollection));
		service.getImdbIdsInDefaultCollection("User");
		verify(movieDao, times(1)).getAttributeValuesForCollection(movieCollection.getId(), ImdbAttribute.IMDB_ID.getKey());
	}
	
	@Test
	public void testGetUnlinkedMovies() throws Exception {
		when(collectionService.getDefaultMovieCollection("User")).thenReturn(Optional.of(movieCollection));
		service.getUnlinkedMoviesForDefaultCollection("User");
		verify(movieDao, times(1)).getMoviesWithoutAttribute(movieCollection.getId(), ImdbAttribute.IMDB_ID.getKey());
	}
}
