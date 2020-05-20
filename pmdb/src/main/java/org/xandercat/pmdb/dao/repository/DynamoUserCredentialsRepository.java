package org.xandercat.pmdb.dao.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.xandercat.pmdb.dto.PmdbUserCredentials;

@EnableScan
public interface DynamoUserCredentialsRepository extends CrudRepository<PmdbUserCredentials, String> {

}
