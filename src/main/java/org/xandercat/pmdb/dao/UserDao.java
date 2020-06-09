package org.xandercat.pmdb.dao;

import java.util.List;
import java.util.Optional;

import org.xandercat.pmdb.dto.PmdbUser;

/**
 * Interface for user operations.
 * 
 * @author Scott Arnold
 */
public interface UserDao {

	/**
	 * Add a new user to the system. 
	 * 
	 * @param user user to add
	 * @param unencryptedPassword new password for user
	 */
	public void addUser(PmdbUser user, String unencryptedPassword);
	
	/**
	 * Re-add a user to the system.  Typically by syncing them in from cloud storage.
	 * Special case of adding a user.  When saving with this method, the encrypted password
	 * is used rather than an unencrypted password.  Ensure that the encrypted password
	 * is set accordingly.
	 * 
	 * @param user user to add
	 */
	public void readdUser(PmdbUser user);
	
	/**
	 * Save changes to existing user.  Should not be called for new users.
	 * Password is not updated within this method call.  To change user
	 * password, call the {@link #changePassword(String, String) changePassword} method.
	 * 
	 * @param user user to save
	 */
	public void saveUser(PmdbUser user);
	
	/**
	 * Change user password.
	 * 
	 * @param username username
	 * @param newPassword new unencrypted password
	 * @return encrypted password
	 */
	public String changePassword(String username, String newPassword);
	
	/**
	 * Returns user of given username.
	 * 
	 * @param username username
	 * @return user of given username
	 */
	public Optional<PmdbUser> getUser(String username);
	
	/**
	 * Returns user with given email address.
	 * @param email email address
	 * @return user with given email address, or null if cannot be determined
	 */
	public Optional<PmdbUser> getUserByEmail(String email);
	
	/**
	 * Update the last access timestamp for the user of given username to the current date and time.
	 * 
	 * @param username username
	 */
	public void updateLastAccess(String username);
	
	/**
	 * Returns a count of the total number of users in the system regardless of user status.
	 * 
	 * @return count of users in the system
	 */
	public int getUserCount();
	
	/**
	 * Returns users with usernames that contain the provided search string.  Search is 
	 * case insensitive.
	 * 
	 * @param searchString search string
	 * @return users with usernames containing the search string
	 */
	public List<PmdbUser> searchUsers(String searchString);
	
	/**
	 * Deletes user of given username.  Operation will fail if there are any other tables
	 * other than the user details table with foreign keys referencing the user.
	 * 
	 * @param username username of user to delete
	 */
	public void delete(String username);
}
