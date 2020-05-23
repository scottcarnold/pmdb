package org.xandercat.pmdb.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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
			jdbcTemplate.update(sql, ps -> {
				ps.setString(1, username);
				ps.setString(2, grantedAuthority.name());
			});
		}
	}

	@Override
	public void revoke(String username, PmdbGrantedAuthority... grantedAuthorities) {
		final String sql = "DELETE FROM authorities WHERE username = ? AND authority = ?";
		for (PmdbGrantedAuthority grantedAuthority : grantedAuthorities) {
			jdbcTemplate.update(sql, ps -> {
				ps.setString(1, username);
				ps.setString(2, grantedAuthority.name());
			});
		}	
	}
	
	@Override
	public Collection<PmdbGrantedAuthority> getAuthorities(String username) {
		Set<PmdbGrantedAuthority> authorities = new HashSet<PmdbGrantedAuthority>();
		final String sql = "SELECT authority FROM authorities WHERE username = ?";
		jdbcTemplate.query(sql, ps -> ps.setString(1, username), rs -> {
			authorities.add(PmdbGrantedAuthority.valueOf(rs.getString(1)));
		});
		return authorities;
	}
}
