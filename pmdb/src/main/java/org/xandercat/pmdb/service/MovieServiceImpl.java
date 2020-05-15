package org.xandercat.pmdb.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.MovieDao;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.exception.CollectionSharingException;
import org.xandercat.pmdb.util.CIStringComparator;
import org.xandercat.pmdb.util.PmdbException;

@Component
public class MovieServiceImpl implements MovieService {

	private Comparator<String> ciStringComparator = new CIStringComparator();
	
	@Autowired
	private MovieDao movieDao;
	
	@Autowired
	private CollectionService collectionService;
	
	@Override
	public Set<Movie> getMoviesForCollection(int collectionId, String callingUsername) throws CollectionSharingException {
		collectionService.assertCollectionViewable(collectionId, callingUsername);
		return movieDao.getMoviesForCollection(collectionId);
	}

	@Override
	public Set<Movie> searchMoviesForCollection(int collectionId, String searchString, String callingUsername)	throws CollectionSharingException {
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

	@Override
	public List<String> getTableColumnPreferences(String callingUsername) {
		return movieDao.getTableColumnPreferences(callingUsername);
	}

	@Override
	public void addTableColumnPreference(String attributeName, String callingUsername) {
		movieDao.addTableColumnPreference(attributeName, callingUsername);
		
	}

	@Override
	public void reorderTableColumnPreference(int sourceIdx, int targetIdx, String callingUsername) throws PmdbException {
		if (sourceIdx == targetIdx) {
			return;
		}
		Integer max = movieDao.getMaxTableColumnPreferenceIndex(callingUsername);
		if (max == null) {
			throw new PmdbException("User has no preferences to reorder.");
		}
		if (sourceIdx < 0 || targetIdx < 0 || sourceIdx > max || targetIdx > max) {
			throw new PmdbException("source index or target index do not fall in acceptable range of 0 to " + max);
		}
		movieDao.reorderTableColumnPreference(sourceIdx, targetIdx, callingUsername);
	}

	@Override
	public void deleteTableColumnPreference(int sourceIdx, String callingUsername) {
		movieDao.deleteTableColumnPreference(sourceIdx, callingUsername);
	}

	@Override
	public List<String> getAttributeKeysForCollection(int collectionId, String callingUsername)	throws CollectionSharingException {
		collectionService.assertCollectionViewable(collectionId, callingUsername);
		List<String> attributeKeys = movieDao.getAttributeKeysForCollection(collectionId);
		Collections.sort(attributeKeys, ciStringComparator);
		return attributeKeys;
	}
}
