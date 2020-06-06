package org.xandercat.pmdb.form.useradmin;

import org.springframework.validation.annotation.Validated;

/**
 * User search form for administrators.  If syncCloud is specified, it indicates that the 
 * administrator wishes to check to see if there are any orphan users in either the cloud 
 * or in the local system.
 * 
 * @author Scott Arnold
 */
@Validated
public class SearchForm {

	private String searchString;
	private boolean syncCloud;

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public boolean isSyncCloud() {
		return syncCloud;
	}

	public void setSyncCloud(boolean syncCloud) {
		this.syncCloud = syncCloud;
	}

}
