package org.xandercat.pmdb.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.xandercat.pmdb.dto.MovieCollection;

/**
 * DynamoDB CRUD repository for movie collection objects.
 *   
 * @author Scott Arnold
 */
public interface DynamoCollectionRepository extends CrudRepository<MovieCollection, String> {

}
