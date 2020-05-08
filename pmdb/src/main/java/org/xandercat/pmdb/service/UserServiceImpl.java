package org.xandercat.pmdb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.dao.AuthDao;
import org.xandercat.pmdb.dao.UserDao;
import org.xandercat.pmdb.dto.PmdbUser;
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
	public int getUserCount() {
		return userDao.getUserCount();
	}
}
