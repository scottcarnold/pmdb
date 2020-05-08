package org.xandercat.pmdb.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;

@Component
public class AuthDaoImpl implements AuthDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public void grant(String username, PmdbGrantedAuthority... grantedAuthorities) {
		grant(username, Arrays.asList(grantedAuthorities));
	}

	@Override
	public void grant(String username, Collection<PmdbGrantedAuthority> grantedAuthorities) {
		final String sql = "INSERT INTO authorities(username, authority) VALUES (?, ?)";
		for (PmdbGrantedAuthority grantedAuthority : grantedAuthorities) {
			jdbcTemplate.update(sql, new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					ps.setString(1, username);
					ps.setString(2, grantedAuthority.name());
				}
			});
		}
	}

	@Override
	public Collection<PmdbGrantedAuthority> getAuthorities(String username) {
		Set<PmdbGrantedAuthority> authorities = new HashSet<PmdbGrantedAuthority>();
		final String sql = "SELECT authority FROM authorities WHERE username = ?";
		jdbcTemplate.query(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, username);
			}	
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				authorities.add(PmdbGrantedAuthority.valueOf(rs.getString(1)));
				
			}
		});
		return authorities;
	}
}
