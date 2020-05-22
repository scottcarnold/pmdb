package org.xandercat.pmdb.form.imdb;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

@Validated
public class SearchForm {

	@NotBlank
	private String title;
	
	private String year;
	
	@Range(min=1)
	private int page = 1;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
}
