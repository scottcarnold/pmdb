package org.xandercat.pmdb.form.movie;

import org.springframework.validation.annotation.Validated;

/**
 * Form for searching a movie collection.
 * 
 * @author Scott Arnold
 */
@Validated
public class SearchForm {

	private String collectionId;
	private String searchString;

	public SearchForm() {
	}
	
	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
