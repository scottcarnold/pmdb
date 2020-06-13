package org.xandercat.pmdb.service;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;
import org.xandercat.pmdb.dao.AuthDao;
import org.xandercat.pmdb.dao.UserDao;
import org.xandercat.pmdb.dao.repository.DynamoUserCredentialsRepository;
import org.xandercat.pmdb.dto.CloudUserSearchResults;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.dto.PmdbUserCredentials;
import org.xandercat.pmdb.exception.WebServicesException;
import org.xandercat.pmdb.exception.ServiceLimitExceededException;
import org.xandercat.pmdb.util.ApplicationProperties;
import org.xandercat.pmdb.util.format.FormatUtil;

@Component
public class UserServiceImpl implements UserService {
	
	@Value("${registrations.interval.max}")
	private int regMax;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AuthDao authDao;
	
	@Autowired
	private DynamoUserCredentialsRepository dynamoUserCredentialsRepository;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Autowired
	private ApplicationService applicationService;
	
	@Override
	public Optional<PmdbUser> getUser(String username) {
		return userDao.getUser(username);
	}

	@Override
	public Optional<PmdbUser> getUserByEmail(String email) {
		return userDao.getUserByEmail(email);
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
	public void saveMyAccountUser(PmdbUser user, String newPassword, String callingUsername) {
		if (!callingUsername.equals(user.getUsername())) {
			throw new IllegalArgumentException("Username mismatch. Form has username " + user.getUsername() + " while authenticated username is " + callingUsername);
		}
		Optional<PmdbUser> storedUserOptional = userDao.getUser(callingUsername);
		if (!storedUserOptional.isPresent()) {
			throw new IllegalArgumentException("User " + callingUsername + " not found.");
		}
		PmdbUser storedUser = storedUserOptional.get();
		storedUser.setGrantedAuthorities(authDao.getAuthorities(storedUser.getUsername()));
		storedUser.setEmail(user.getEmail());
		storedUser.setFirstName(user.getFirstName());
		storedUser.setLastName(user.getLastName());
		saveUser(storedUser, newPassword, false);
	}

	@Override
	public void saveUser(PmdbUser user, String newPassword, boolean newUser) {
		if (FormatUtil.isBlank(user.getUsername())) {
			throw new IllegalArgumentException("No username provided.");
		}
		String username = user.getUsername().trim();
		newPassword = FormatUtil.isBlank(newPassword)? null : newPassword.trim();
		if (newUser) {
			if (getUser(username).isPresent()) {
				throw new IllegalArgumentException("User " + username + " already exists.");
			}
			if (applicationProperties.isAwsEnabled()) {
				Optional<PmdbUserCredentials> creds = dynamoUserCredentialsRepository.findById(username);
				if (creds.isPresent()) {
					throw new IllegalArgumentException("Username already exists in the cloud; user must be synced or removed from cloud to add again.");
				}
			}
		} else {
			if (!getUser(username).isPresent()) {
				throw new IllegalArgumentException("User " + username + " not found.");
			}
		}
		if (newUser) {
			userDao.addUser(user, newPassword);
			authDao.grant(user.getUsername(), user.getGrantedAuthorities());
			if (applicationProperties.isAwsEnabled()) {
				PmdbUserCredentials credentials = new PmdbUserCredentials(user.getUsername(), user.getPassword().getBytes());
				dynamoUserCredentialsRepository.save(credentials);
			}
		} else {
			userDao.saveUser(user);
			if (FormatUtil.isNotBlank(newPassword)) {
				String encryptedPassword = userDao.changePassword(username, newPassword);
				if (applicationProperties.isAwsEnabled()) {
					PmdbUserCredentials credentials = new PmdbUserCredentials(username, encryptedPassword.getBytes());
					dynamoUserCredentialsRepository.save(credentials);			
				}
			}
			boolean isAdmin = isAdministrator(username);
			if (user.getGrantedAuthorities().contains(PmdbGrantedAuthority.ROLE_ADMIN) && !isAdmin) {
				authDao.grant(username, PmdbGrantedAuthority.ROLE_ADMIN);
			} else if (!user.getGrantedAuthorities().contains(PmdbGrantedAuthority.ROLE_ADMIN) && isAdmin) {
				authDao.revoke(username, PmdbGrantedAuthority.ROLE_ADMIN);
			}
		}
	}
	
	@Override
	public void registerUser(PmdbUser user, String newPassword) throws ServiceLimitExceededException {
		int count = applicationService.incrementRegistrationsTriggerCount();
		if (count >= regMax) {
			throw new ServiceLimitExceededException("Too many registrations in a short period of time.", count, regMax);
		}
		if (!FormatUtil.isValidUsername(user.getUsername())) {
			// defensive double check on username, though validation should have already caught this
			throw new IllegalArgumentException("Invalid username: \"" + user.getUsername() + "\"");
		}
		if (FormatUtil.isBlank(newPassword)) {
			// defensive double check on password, though validation should have already caught this
			throw new IllegalArgumentException("Password cannot be empty.");
		}
		user.setEnabled(false); // ensure user account is initially disabled
		user.setGrantedAuthorities(PmdbGrantedAuthority.ROLE_USER); // ensure proper roles
		saveUser(user, newPassword, true);
		//TODO: If we had email working, we could send an account activation email to the user here
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
		Set<String> cloudUsernames = new HashSet<String>();
		credentials.forEach(credential -> cloudUsernames.add(credential.getUsername()));
		Set<String> localUsernames = regularSearchResults.stream()
				.map(user -> user.getUsername())
				.collect(Collectors.toSet());
		Set<String> usersNotInCloud = localUsernames.stream()
				.filter(localUsername -> !cloudUsernames.contains(localUsername))
				.collect(Collectors.toSet());
		Set<String> onlyInCloudUsernames = cloudUsernames.stream()
				.filter(cloudUsername -> cloudUsername.contains(searchString))
				.filter(cloudUsername -> !localUsernames.contains(cloudUsername))
				.collect(Collectors.toSet());
		results.setUsernamesNotInCloud(usersNotInCloud);
		results.setUsernamesOnlyInCloud(onlyInCloudUsernames);
		return results;
	}

	@Override
	public void syncUserToCloud(String username) throws WebServicesException {
		if (applicationProperties.isAwsEnabled()) {
			PmdbUser user = userDao.getUser(username).get();
			PmdbUserCredentials credentials = new PmdbUserCredentials(user.getUsername(), user.getPassword().getBytes());
			try {
				dynamoUserCredentialsRepository.save(credentials);
			} catch (Exception e) {
				throw new WebServicesException(e);
			}
		} else {
			throw new WebServicesException("Cloud services are disabled.");
		}
	}

	@Override
	public void syncUserFromCloud(String username) throws WebServicesException {
		if (userDao.getUser(username) != null) {
			throw new IllegalArgumentException("User named " + username + " already exists in the system.");
		}
		if (applicationProperties.isAwsEnabled()) {
			Optional<PmdbUserCredentials> optional = dynamoUserCredentialsRepository.findById(username);
			if (optional.isPresent()) {
				PmdbUserCredentials creds = optional.get();
				PmdbUser user = new PmdbUser(creds.getUsername());
				try {
					user.setPassword(new String(creds.getPassword(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					throw new UnsupportedOperationException(e);
				}
				userDao.readdUser(user);
				authDao.grant(user.getUsername(), PmdbGrantedAuthority.ROLE_USER);
			} else {
				throw new WebServicesException("Unable to find user " + username + " in the cloud.");
			}
		} else {
			throw new WebServicesException("Cloud services are disabled.");
		}		
	}

	@Override
	public void deleteUser(String username) {
		Optional<PmdbUser> user = getUser(username);
		if (!user.isPresent()) {
			throw new IllegalArgumentException("User not found.");
		}
		if (user.get().getLastAccessDate() != null) {
			throw new IllegalArgumentException("Only users who have no last access date (indicating they have never logged in) can be deleted.");
		}
		authDao.revoke(username, PmdbGrantedAuthority.values());
		userDao.delete(username);
		if (applicationProperties.isAwsEnabled()) {
			PmdbUserCredentials userCredentials = new PmdbUserCredentials(user.get().getUsername(), user.get().getPassword().getBytes());
			dynamoUserCredentialsRepository.delete(userCredentials);
		}
	}
}
