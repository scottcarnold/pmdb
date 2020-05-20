package org.xandercat.pmdb.dao.repository;

import org.springframework.data.repository.CrudRepository;
import org.xandercat.pmdb.dto.PmdbUserCredentials;

public interface DynamoUserCredentialsRepository extends CrudRepository<PmdbUserCredentials, String> {

}
