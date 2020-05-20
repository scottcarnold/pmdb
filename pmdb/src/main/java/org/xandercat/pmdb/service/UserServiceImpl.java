package org.xandercat.pmdb.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;
import org.xandercat.pmdb.dao.AuthDao;
import org.xandercat.pmdb.dao.UserDao;
import org.xandercat.pmdb.dao.repository.DynamoUserCredentialsRepository;
import org.xandercat.pmdb.dto.CloudUserSearchResults;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.dto.PmdbUserCredentials;
import org.xandercat.pmdb.exception.CloudServicesException;
import org.xandercat.pmdb.exception.PmdbException;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.util.ApplicationProperties;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AuthDao authDao;
	
	@Autowired
	private DynamoUserCredentialsRepository dynamoUserCredentialsRepository;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Override
	public PmdbUser getUser(String username) {
		return userDao.getUser(username);
	}

	@Override
	public PmdbUser getUserByEmail(String email) {
		return userDao.getUserByEmail(email);
	}

	@Override
	public void addUser(PmdbUser user, String unencryptedPassword) throws PmdbException {
		userDao.addUser(user, unencryptedPassword);
		authDao.grant(user.getUsername(), user.getGrantedAuthorities());
		if (applicationProperties.isAwsEnabled()) {
			PmdbUserCredentials credentials = new PmdbUserCredentials(user.getUsername(), user.getPassword().getBytes());
			dynamoUserCredentialsRepository.save(credentials);
		}
	}

	@Override
	public void updateLastAccess(String username) {
		userDao.updateLastAccess(username);
	}
	
	@Override
	public int getUserCount() {
		return userDao.getUserCount();
	}

	@Override
	public List<PmdbUser> searchUsers(String searchString) {
		return userDao.searchUsers(searchString);
	}

	@Override
	public void saveMyAccountUser(UserForm userForm, String callingUsername) throws PmdbException {
		if (!callingUsername.equals(userForm.getUsername())) {
			throw new PmdbException("Username mismatch. Form has username " + userForm.getUsername() + " while authenticated username is " + callingUsername);
		}
		PmdbUser user = userDao.getUser(callingUsername);
		if (user == null) {
			throw new PmdbException("User " + callingUsername + " not found.");
		}
		userForm.setAdministrator(isAdministrator(callingUsername));
		userForm.setEnabled(user.isEnabled());
		saveUser(userForm, false);
	}

	@Override
	public void saveUser(UserForm userForm, boolean newUser) throws PmdbException {
		if (StringUtils.isEmptyOrWhitespace(userForm.getUsername())) {
			throw new PmdbException("No username provided.");
		}
		String username = userForm.getUsername().trim();
		PmdbUser user = getUser(username);
		if (newUser) {
			if (user != null) {
				throw new PmdbException("User " + username + " already exists.");
			}
			if (applicationProperties.isAwsEnabled()) {
				Optional<PmdbUserCredentials> creds = dynamoUserCredentialsRepository.findById(username);
				if (creds.isPresent()) {
					throw new PmdbException("Username already exists in the cloud; user must be synced or removed from cloud to add again.");
				}
			}
			user = new PmdbUser(username);
		} else {
			if (user == null) {
				throw new PmdbException("User " + username + " not found.");
			}
		}
		user.setFirstName(userForm.getFirstName().trim());
		user.setLastName(userForm.getLastName().trim());
		user.setEmail(userForm.getEmail().trim());
		user.setEnabled(userForm.isEnabled());
		if (newUser) {
			userDao.addUser(user, userForm.getPasswordPair().getFirst().trim());
			authDao.grant(username, PmdbGrantedAuthority.ROLE_USER);
			if (userForm.isAdministrator()) {
				authDao.grant(username, PmdbGrantedAuthority.ROLE_ADMIN);
			}
			if (applicationProperties.isAwsEnabled()) {
				PmdbUserCredentials credentials = new PmdbUserCredentials(user.getUsername(), user.getPassword().getBytes());
				dynamoUserCredentialsRepository.save(credentials);
			}
		} else {
			userDao.saveUser(user);
			if (!StringUtils.isEmptyOrWhitespace(userForm.getPasswordPair().getFirst())) {
				String encryptedPassword = userDao.changePassword(username, userForm.getPasswordPair().getFirst().trim());
				if (applicationProperties.isAwsEnabled()) {
					PmdbUserCredentials credentials = new PmdbUserCredentials(username, encryptedPassword.getBytes());
					dynamoUserCredentialsRepository.save(credentials);			
				}
			}
			boolean isAdmin = isAdministrator(username);
			if (userForm.isAdministrator() && !isAdmin) {
				authDao.grant(username, PmdbGrantedAuthority.ROLE_ADMIN);
			} else if (!userForm.isAdministrator() && isAdmin) {
				authDao.revoke(username, PmdbGrantedAuthority.ROLE_ADMIN);
			}
		}
	}
	
	@Override
	public boolean isAdministrator(String username) {
		Collection<PmdbGrantedAuthority> authorities = authDao.getAuthorities(username);
		return authorities.contains(PmdbGrantedAuthority.ROLE_ADMIN);
	}

	@Override
	public CloudUserSearchResults syncCloudUsers(List<PmdbUser> regularSearchResults, String searchString) {
		CloudUserSearchResults results = new CloudUserSearchResults();
		Iterable<PmdbUserCredentials> credentials = dynamoUserCredentialsRepository.findAll();
		Set<String> localUsernames = new HashSet<String>();
		Set<String> cloudUsernames = new HashSet<String>();
		credentials.forEach(credential -> cloudUsernames.add(credential.getUsername()));
		regularSearchResults.forEach(pmdbUser -> localUsernames.add(pmdbUser.getUsername()));
		Set<String> usersNotInCloud = new HashSet<String>();
		usersNotInCloud.addAll(localUsernames);
		usersNotInCloud.removeAll(cloudUsernames);
		for (Iterator<String> cloudUsernamesIter = cloudUsernames.iterator(); cloudUsernamesIter.hasNext();) {
			String cloudUsername = cloudUsernamesIter.next();
			if (!cloudUsername.contains(searchString)) {
				cloudUsernamesIter.remove();
			}
		}
		cloudUsernames.removeAll(localUsernames);
		results.setUsernamesNotInCloud(usersNotInCloud);
		results.setUsernamesOnlyInCloud(cloudUsernames);
		return results;
	}

	@Override
	public void syncUserToCloud(String username) throws CloudServicesException {
		if (applicationProperties.isAwsEnabled()) {
			PmdbUser user = userDao.getUser(username);
			PmdbUserCredentials credentials = new PmdbUserCredentials(user.getUsername(), user.getPassword().getBytes());
			try {
				dynamoUserCredentialsRepository.save(credentials);
			} catch (Exception e) {
				throw new CloudServicesException(e);
			}
		} else {
			throw new CloudServicesException("Cloud services are disabled.");
		}
	}

	@Override
	public void syncUserFromCloud(String username) throws CloudServicesException, PmdbException {
		if (userDao.getUser(username) != null) {
			throw new IllegalArgumentException("User named " + username + " already exists in the system.");
		}
		if (applicationProperties.isAwsEnabled()) {
			Optional<PmdbUserCredentials> optional = dynamoUserCredentialsRepository.findById(username);
			if (optional.isPresent()) {
				PmdbUserCredentials creds = optional.get();
				PmdbUser user = new PmdbUser(creds.getUsername());
				userDao.readdUser(user, creds.getPassword());
				authDao.grant(user.getUsername(), PmdbGrantedAuthority.ROLE_USER);
			} else {
				throw new CloudServicesException("Unable to find user " + username + " in the cloud.");
			}
		} else {
			throw new CloudServicesException("Cloud services are disabled.");
		}		
	}	
}
