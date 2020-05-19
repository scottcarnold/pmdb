package org.xandercat.pmdb.dao.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dto.PmdbUserCredentials;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

@Component
public class AwsUserDaoImpl implements AwsUserDao {
	
	@Autowired
	private DynamoDBMapper ddbm;
	
	@Override
	public void addUserCredentials(PmdbUserCredentials credentials) {
		ddbm.save(credentials);
	}

	@Override
	public void changeUserPassword(PmdbUserCredentials credentials) {
		ddbm.save(credentials);
	}

}
