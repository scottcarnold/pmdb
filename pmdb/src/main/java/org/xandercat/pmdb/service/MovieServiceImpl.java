package org.xandercat.pmdb.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.exception.CollectionSharingException;

@Component
public class MovieServiceImpl implements MovieService {

	@Autowired
	private MovieDao movieDao;
	
	@Autowired
	private CollectionService collectionService;
	
	@Override
	public List<Movie> getMoviesForCollection(int collectionId, String callingUsername) throws CollectionSharingException {
		collectionService.assertCollectionViewable(collectionId, callingUsername);
		return movieDao.getMoviesForCollection(collectionId);
	}

	@Override
	public List<Movie> searchMoviesForCollection(int collectionId, String searchString, String callingUsername)	throws CollectionSharingException {
		collectionService.assertCollectionViewable(collectionId, callingUsername);
		return movieDao.searchMoviesForCollection(collectionId, searchString);
	}

	@Override
	public Movie getMovie(int id, String callingUsername) throws CollectionSharingException {
		Movie movie = movieDao.getMovie(id);
		if (movie != null) {
			collectionService.assertCollectionViewable(movie.getCollectionId(), callingUsername);
		}
		return movie;
	}

	@Override
	public void addMovie(Movie movie, String callingUsername) throws CollectionSharingException {
		collectionService.assertCollectionEditable(movie.getCollectionId(), callingUsername);
		movieDao.addMovie(movie);
	}

	@Override
	public void updateMovie(Movie movie, String callingUsername) throws CollectionSharingException {
		collectionService.assertCollectionEditable(movie.getCollectionId(), callingUsername);
		movieDao.updateMovie(movie);
	}

	@Override
	public void deleteMovie(int id, String callingUsername) throws CollectionSharingException {
		Movie movie = movieDao.getMovie(id);
		collectionService.assertCollectionEditable(movie.getCollectionId(), callingUsername);
		movieDao.deleteMovie(id);
	}
}
