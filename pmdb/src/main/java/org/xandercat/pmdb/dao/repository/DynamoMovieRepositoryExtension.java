package org.xandercat.pmdb.dao.repository;

import java.util.List;
import java.util.Set;

import org.xandercat.pmdb.dto.Movie;

public interface DynamoMovieRepositoryExtension {

	public Set<Movie> searchMoviesForCollection(String collectionId, String searchString);
	
	public List<String> getAttributeKeysForCollection(String collectionId);
	
	public Set<String> getAttributeValuesForCollection(String collectionId, String attributeName);
}
