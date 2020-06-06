package org.xandercat.pmdb.dao;

import java.util.Collection;

import org.xandercat.pmdb.config.PmdbGrantedAuthority;

/**
 * Interface for authentication operations.
 * 
 * @author Scott Arnold
 */
public interface AuthDao {

	/**
	 * Grant the provided authorities to the provided user.
	 * 
	 * @param username            user to grant authorities to
	 * @param grantedAuthorities  authorities to grant
	 */
	public void grant(String username, PmdbGrantedAuthority... grantedAuthorities);
	
	/**
	 * Grant the provided authorities to the provided user.
	 * 
	 * @param username            user to grant authorities to
	 * @param grantedAuthorities  authorities to grant
	 */
	public void grant(String username, Collection<PmdbGrantedAuthority> grantedAuthorities);
	
	/**
	 * Revoke the provided authorities from the provided user.
	 * 
	 * @param username            user to revoke authorities from
	 * @param grantedAuthorities  authorities to revoke
	 */
	public void revoke(String username, PmdbGrantedAuthority... grantedAuthorities);
	
	/**
	 * Returns collection of authorities for the given user.
	 * 
	 * @param username  user to get authorities for
	 * 
	 * @return collection of authorities for user
	 */
	public Collection<PmdbGrantedAuthority> getAuthorities(String username);
}
