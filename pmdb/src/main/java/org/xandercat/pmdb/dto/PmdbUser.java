package org.xandercat.pmdb.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.xandercat.pmdb.config.PmdbGrantedAuthority;

/**
 * User class that also doubles as UserDetails for Spring security.
 * 
 * @author Scott Arnold
 */
public class PmdbUser implements UserDetails {

	private static final long serialVersionUID = 563446069682826416L;
	
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String email;
	private boolean enabled;
	private Date createdDate;
	private Date updatedDate;
	private Date lastAccessDate;
	private Set<PmdbGrantedAuthority> grantedAuthorities = new HashSet<PmdbGrantedAuthority>();
	
	public PmdbUser() {
	}
	public PmdbUser(String username) {
		this.username = username;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Date getLastAccessDate() {
		return lastAccessDate;
	}
	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Set<PmdbGrantedAuthority> getGrantedAuthorities() {
		return grantedAuthorities;
	}
	public void setGrantedAuthorities(Collection<PmdbGrantedAuthority> grantedAuthorities) {
		this.grantedAuthorities.clear();
		this.grantedAuthorities.addAll(grantedAuthorities);
	}
	public void setGrantedAuthorities(PmdbGrantedAuthority... grantedAuthorities) {
		setGrantedAuthorities(Arrays.asList(grantedAuthorities));
	}
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.unmodifiableCollection(grantedAuthorities);
	}
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
}
