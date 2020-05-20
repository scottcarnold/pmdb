package org.xandercat.pmdb.dto;

import java.util.Set;

public class CloudUserSearchResults {

	private Set<String> usernamesNotInCloud;
	private Set<String> usernamesOnlyInCloud;
	
	public Set<String> getUsernamesNotInCloud() {
		return usernamesNotInCloud;
	}
	public void setUsernamesNotInCloud(Set<String> usernamesNotInCloud) {
		this.usernamesNotInCloud = usernamesNotInCloud;
	}
	public Set<String> getUsernamesOnlyInCloud() {
		return usernamesOnlyInCloud;
	}
	public void setUsernamesOnlyInCloud(Set<String> usernamesOnlyInCloud) {
		this.usernamesOnlyInCloud = usernamesOnlyInCloud;
	}

}
