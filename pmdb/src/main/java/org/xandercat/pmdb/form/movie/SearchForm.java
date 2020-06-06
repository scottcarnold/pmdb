package org.xandercat.pmdb.form.movie;

import org.springframework.validation.annotation.Validated;

/**
 * Form for searching a movie collection.
 * 
 * @author Scott Arnold
 */
@Validated
public class SearchForm {

	private String searchString;

	public SearchForm() {
	}
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
