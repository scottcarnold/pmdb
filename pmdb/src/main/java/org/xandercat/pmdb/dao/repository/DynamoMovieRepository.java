package org.xandercat.pmdb.dao.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.xandercat.pmdb.dto.Movie;

public interface DynamoMovieRepository extends CrudRepository<Movie, String>, DynamoMovieRepositoryExtension {

	public List<Movie> findByCollectionId(String collectionId); // should be implemented automatically by spring-data-dynamodb
	
	public void deleteByCollectionId(String collectionId); //TODO: might need to implement this one myself
	

}
