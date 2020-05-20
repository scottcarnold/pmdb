package org.xandercat.pmdb.dao;

import java.util.List;

import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.exception.PmdbException;

public interface UserDao {

	public void addUser(PmdbUser user, String unencryptedPassword) throws PmdbException;
	
	public void readdUser(PmdbUser user, byte[] encryptedPassword) throws PmdbException;
	
	public void saveUser(PmdbUser user);
	
	public String changePassword(String username, String newPassword);
	
	public PmdbUser getUser(String username);
	
	public PmdbUser getUserByEmail(String email);
	
	public void updateLastAccess(String username);
	
	public int getUserCount();
	
	public List<PmdbUser> searchUsers(String searchString);
}
