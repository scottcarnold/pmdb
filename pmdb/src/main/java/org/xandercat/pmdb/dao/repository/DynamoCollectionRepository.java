package org.xandercat.pmdb.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.xandercat.pmdb.dto.MovieCollection;

public interface DynamoCollectionRepository extends CrudRepository<MovieCollection, String> {

}
