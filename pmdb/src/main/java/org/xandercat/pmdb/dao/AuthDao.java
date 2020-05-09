package org.xandercat.pmdb.dao;

import java.util.Collection;

import org.xandercat.pmdb.config.PmdbGrantedAuthority;

public interface AuthDao {

	public void grant(String username, PmdbGrantedAuthority... grantedAuthorities);
	
	public void grant(String username, Collection<PmdbGrantedAuthority> grantedAuthorities);
	
	public void revoke(String username, PmdbGrantedAuthority... grantedAuthorities);
	
	public Collection<PmdbGrantedAuthority> getAuthorities(String username);
}
