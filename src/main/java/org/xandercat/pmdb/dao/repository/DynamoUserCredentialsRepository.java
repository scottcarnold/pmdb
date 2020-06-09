package org.xandercat.pmdb.dao.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.xandercat.pmdb.dto.PmdbUserCredentials;

/**
 * DynamoDB CRUD repository for user credentials.  Scan is enabled on this repository to aid in providing
 * user synchronization functions with expectation that user base will be of reasonably limited size.
 * 
 * @author Scott Arnold
 */
@EnableScan
public interface DynamoUserCredentialsRepository extends CrudRepository<PmdbUserCredentials, String> {

}
