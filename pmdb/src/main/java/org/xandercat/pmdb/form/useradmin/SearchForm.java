package org.xandercat.pmdb.form.useradmin;

import org.springframework.validation.annotation.Validated;

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
