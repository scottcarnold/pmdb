package org.xandercat.pmdb.dao;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.PmdbUser;
import org.xandercat.pmdb.util.DBUtil;
import org.xandercat.pmdb.util.PmdbException;

@Component
public class UserDaoImpl implements UserDao {
	
	private static final Logger LOGGER = LogManager.getLogger(UserDaoImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public void addUser(PmdbUser user, String unencryptedPassword) throws PmdbException {
		LOGGER.info("Request to add user: " + user.getUsername());
		if (StringUtils.isEmptyOrWhitespace(user.getUsername())) {
			throw new PmdbException("Username cannot be empty.");
		}
		if (StringUtils.isEmptyOrWhitespace(unencryptedPassword)) {
			throw new PmdbException("Password cannot be empty.");
		}
		String encryptedPassword = passwordEncoder.encode(unencryptedPassword);
		final String sql = "INSERT INTO users(username, password, enabled) VALUES (?, ?, ?)";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, user.getUsername());
				ps.setBytes(2, encryptedPassword.getBytes());
				ps.setBoolean(3, user.isEnabled());
			}
		});
		user.setPassword(encryptedPassword);
		final String detailsSql = "INSERT INTO user_details(username, firstName, lastName, email, createdTs, updatedTs) VALUES (?, ?, ?, ?, ?, ?)";
		Date now = new Date();
		jdbcTemplate.update(detailsSql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, user.getUsername());
				ps.setString(2, user.getFirstName());
				ps.setString(3, user.getLastName());
				ps.setString(4, user.getEmail());
				DBUtil.setGMTTimestamp(ps, 5, now);
				DBUtil.setGMTTimestamp(ps, 6, now);
			}
		});
	}

	@Override
	public PmdbUser getUser(String username) {
		LOGGER.info("Request to get user: " + username);
		PmdbUser pmdbUser = new PmdbUser();
		final String sql = "SELECT users.username, password, enabled, firstName, lastName, email, createdTs, updatedTs, lastAccessTs FROM users"
				+ " INNER JOIN user_details ON users.username = user_details.username"
				+ " WHERE users.username = ?";
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				pmdbUser.setUsername(rs.getString(1));
				try {
					pmdbUser.setPassword(new String(rs.getBytes(2), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("Unable to read password hash from database.", e);
				} 
				pmdbUser.setEnabled(rs.getBoolean(3));
				pmdbUser.setFirstName(rs.getString(4));
				pmdbUser.setLastName(rs.getString(5));
				pmdbUser.setEmail(rs.getString(6));
				pmdbUser.setCreatedDate(DBUtil.getDateFromGMTTimestamp(rs, 7));
				pmdbUser.setUpdatedDate(DBUtil.getDateFromGMTTimestamp(rs, 8));
				pmdbUser.setLastAccessDate(DBUtil.getDateFromGMTTimestamp(rs, 9));
			}
		});
		return (pmdbUser.getUsername() == null)? null : pmdbUser;
	}

	@Override
	public void updateLastAccess(String username) {
		final String sql = "UPDATE user_details SET lastAccessTs = ? WHERE username = ?";
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				DBUtil.setGMTTimestamp(ps, 1, new Date());
				ps.setString(2, username);
			}
		});
	}

	@Override
	public int getUserCount() {
		return jdbcTemplate.queryForObject("select count(*) from users", Integer.class).intValue();
	}

	@Override
	public List<PmdbUser> searchUsers(String searchString) {
		final String lcSearchString = searchString.trim().toLowerCase();
		final List<PmdbUser> users = new ArrayList<PmdbUser>();
		final String sql = "SELECT users.username, password, enabled, firstName, lastName, email, createdTs, updatedTs, lastAccessTs FROM users"
				+ " INNER JOIN user_details ON users.username = user_details.username"
				+ " WHERE LOWER(users.username) like ?"
				+ " OR LOWER(user_details.firstName) like ?"
				+ " OR LOWER(user_details.lastName) like ?"
				+ " ORDER BY LOWER(users.username)";
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, "%" + lcSearchString + "%");
				ps.setString(2, "%" + lcSearchString + "%");
				ps.setString(3, "%" + lcSearchString + "%");
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				PmdbUser pmdbUser = new PmdbUser();
				pmdbUser.setUsername(rs.getString(1));
				try {
					pmdbUser.setPassword(new String(rs.getBytes(2), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					LOGGER.error("Unable to read password hash from database.", e);
				} 
				pmdbUser.setEnabled(rs.getBoolean(3));
				pmdbUser.setFirstName(rs.getString(4));
				pmdbUser.setLastName(rs.getString(5));
				pmdbUser.setEmail(rs.getString(6));
				pmdbUser.setCreatedDate(DBUtil.getDateFromGMTTimestamp(rs, 7));
				pmdbUser.setUpdatedDate(DBUtil.getDateFromGMTTimestamp(rs, 8));
				pmdbUser.setLastAccessDate(DBUtil.getDateFromGMTTimestamp(rs, 9));
				users.add(pmdbUser);
			}
		});		
		return users;
	}
}
