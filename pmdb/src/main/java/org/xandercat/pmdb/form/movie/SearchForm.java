package org.xandercat.pmdb.form.movie;

import org.springframework.validation.annotation.Validated;

@Validated
public class SearchForm {

	private String searchString;
	private boolean editMode;

	public SearchForm() {
	}
	
	public SearchForm(boolean editMode) {
		this.editMode = editMode;
	}
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

}
