package org.xandercat.pmdb.form.useradmin;

import org.springframework.validation.annotation.Validated;

@Validated
public class SearchForm {

	private String searchString;

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
