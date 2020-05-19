package org.xandercat.pmdb.dao.aws;

import org.xandercat.pmdb.dto.PmdbUserCredentials;

public interface AwsUserDao {

	public void addUserCredentials(PmdbUserCredentials credentials);
	
	public void changeUserPassword(PmdbUserCredentials credentials);
	
	//public void deleteUserCredentials(String username);
	
	//public String getUserEncryptedPassword(String username);
}
