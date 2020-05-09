package org.xandercat.pmdb.dao;

import java.util.List;

import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.util.PmdbException;

public interface UserDao {

	public void addUser(PmdbUser user, String unencryptedPassword) throws PmdbException;
	
	public void saveUser(PmdbUser user);
	
	public void changePassword(String username, String newPassword);
	
	public PmdbUser getUser(String username);
	
	public void updateLastAccess(String username);
	
	public int getUserCount();
	
	public List<PmdbUser> searchUsers(String searchString);
}
