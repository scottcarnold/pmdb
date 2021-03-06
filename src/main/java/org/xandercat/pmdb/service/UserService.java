package org.xandercat.pmdb.service;

import java.util.List;
import java.util.Optional;

import org.xandercat.pmdb.dto.CloudUserSearchResults;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;

public interface UserService {

	/**
	 * Returns a user by username.  Returned user will not contain granted authorities information.
	 * 
	 * @param username   username
	 * 
	 * @return user object for user (will not contain granted authorities)
	 */
	public Optional<PmdbUser> getUser(String username);
	
	/**
	 * Returns a user by email address.
	 * 
	 * @param email  user email address
	 * @return user with provided email address, or null if user could not be determined
	 */
	public Optional<PmdbUser> getUserByEmail(String email);
	
	/**
	 * Save user from the My Account page. When saved through this method, additional validation is performed
	 * to ensure user is authentic and no administrator properties have been changed.
	 * 
	 * @param user             user
	 * @param newPassword      new password; leave null/empty if password should not be changed
	 * @param callingUsername  calling username
	 */
	public void saveMyAccountUser(PmdbUser user, String newPassword, String callingUsername);
	
	/**
	 * Save user.
	 * 
	 * @param user user to save
	 * @param newPassword new password; leave null/empty if password should not be changed
	 * @param newUser whether or not this is a new user
	 */
	public void saveUser(PmdbUser user, String newPassword, boolean newUser);
	
	/**
	 * Register user.
	 * 
	 * @param user user to register
	 * @param newPassword new password
	 * @throws ServiceLimitExceededException if too many user registrations in short period of time
	 */
	public void registerUser(PmdbUser user, String newPassword) throws ServiceLimitExceededException;
	
		
	/**
	 * Update the last access timestamp for a user to the current date.  Call this when a user logs in.
	 * 
	 * @param username  username of user
	 */
	public void updateLastAccess(String username);
	
	/**
	 * Returns count of the number of users in the system, regardless of any status.
	 * 
	 * @return  number of users in the system
	 */
	public int getUserCount();
	
	/**
	 * Returns list of users whose usernames contain the provided search string.  Search is case insensitive.
	 * 
	 * @param searchString search string
	 * @return list of users matching search string
	 */
	public List<PmdbUser> searchUsers(String searchString);
	
	/**
	 * Returns whether or not the user with given username is considered an administrator.
	 * 
	 * @param username username
	 * @return whether or not the user with given username is considered an administrator
	 */
	public boolean isAdministrator(String username);
	
	/**
	 * Returns information on what users from the provided list are not in the cloud, and what users in 
	 * the cloud matching the provided search string are not in the provided list.
	 * 
	 * @param regularSearchResults list of users to check against
	 * @param searchString search string used to collect the list of users
	 * @return information on users who are only in the cloud or only not in the cloud.
	 */
	public CloudUserSearchResults syncCloudUsers(List<PmdbUser> regularSearchResults, String searchString);
	
	/**
	 * Copies a user from PMDB to the cloud.  User should not already be in the cloud when calling this method.
	 * 
	 * @param username username
	 * @throws WebServicesException if there are any web service failures
	 */
	public void syncUserToCloud(String username) throws WebServicesException;
	
	/**
	 * Copies a user from the cloud to PMDB.  User should not already be in PMDB when calling this method.
	 * Created user will not be enabled and will not have any user details set.
	 * 
	 * @param username username
	 * @throws WebServicesException if there are any web service failures
	 */
	public void syncUserFromCloud(String username) throws WebServicesException;
	
	/**
	 * Delete a user from the system.  By design, this should only be called for users who have never logged in.
	 * If user has logged in before, an exception will be thrown.
	 * 
	 * @param username username
	 */
	public void deleteUser(String username);
}
