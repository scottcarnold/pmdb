package org.xandercat.pmdb.service;

import java.util.List;

import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.util.PmdbException;

public interface UserService {

	/**
	 * Returns a user by username.  Returned user will not contain granted authorities information.
	 * 
	 * @param username   username
	 * 
	 * @return user object for user (will not contain granted authorities)
	 */
	public PmdbUser getUser(String username);
	
	/**
	 * Add a new user.  This method should save any stored authorities.  However, the password in the user
	 * object is meant to be the encrypted form which is why the unencrypted password exists as a separate
	 * argument.  The password in the user object will be updated to the encrypted form.
	 * 
	 * @param user
	 * @param unencryptedPassword
	 * @throws PmdbException
	 */
	public void addUser(PmdbUser user, String unencryptedPassword) throws PmdbException;
	
	/**
	 * Save user from the My Account page. When saved through this method, additional validation is performed
	 * to ensure user is authentic and no administrator properties have been changed.
	 * 
	 * @param userForm
	 * @param callingUsername
	 * @throws PmdbException
	 */
	public void saveMyAccountUser(UserForm userForm, String callingUsername) throws PmdbException;
	
	public void saveUser(UserForm userForm, boolean newUser) throws PmdbException;
	
	/**
	 * Update the last access timestamp for a user to the current date.  Call this when a user logs in.
	 * 
	 * @param username
	 */
	public void updateLastAccess(String username);
	
	/**
	 * Returns count of the number of users in the system, regardless of any status.
	 * 
	 * @return  number of users in the system
	 */
	public int getUserCount();
	
	public List<PmdbUser> searchUsers(String searchString);
	
	public boolean isAdministrator(String username);
}
