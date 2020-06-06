package org.xandercat.pmdb.dao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.xandercat.pmdb.dto.Movie;

/**
 * DynamoDB CRUD repository for movies, with additional functions for searching and retrieving other details.
 * 
 * @author Scott Arnold
 */
public interface DynamoMovieRepository extends CrudRepository<Movie, String>, DynamoMovieRepositoryExtension {

	/**
	 * Find list of all movies of given movie collection id.
	 * 
	 * @param collectionId  movie collection id
	 * 
	 * @return list of movies for movie collection
	 */
	public List<Movie> findByCollectionId(String collectionId); // implemented automatically by spring-data-dynamodb
	
	/**
	 * Delete all movies of given movie collection id.
	 * 
	 * @param collectionId  movie collection id
	 */
	public void deleteByCollectionId(String collectionId); // implemented automatically by spring-data-dynamodb
	

}
