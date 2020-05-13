package org.xandercat.pmdb.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;
import org.xandercat.pmdb.dao.AuthDao;
import org.xandercat.pmdb.dao.UserDao;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.form.useradmin.UserForm;
import org.xandercat.pmdb.util.PmdbException;

@Component
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private AuthDao authDao;
	


	@Override
	public PmdbUser getUser(String username) {
		return userDao.getUser(username);
	}

	@Override
	public void addUser(PmdbUser user, String unencryptedPassword) throws PmdbException {
		userDao.addUser(user, unencryptedPassword);
		authDao.grant(user.getUsername(), user.getGrantedAuthorities());
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
		} else {
			userDao.saveUser(user);
			if (!StringUtils.isEmptyOrWhitespace(userForm.getPasswordPair().getFirst())) {
				userDao.changePassword(username, userForm.getPasswordPair().getFirst().trim());
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
}
