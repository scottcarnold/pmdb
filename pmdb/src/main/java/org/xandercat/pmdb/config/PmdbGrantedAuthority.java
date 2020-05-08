package org.xandercat.pmdb.config;

import org.springframework.security.core.GrantedAuthority;

public enum PmdbGrantedAuthority implements GrantedAuthority {

	ROLE_USER, ROLE_ADMIN;

	@Override
	public String getAuthority() {
		return name();
	}
}
