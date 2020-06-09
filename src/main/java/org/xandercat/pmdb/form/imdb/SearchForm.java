package org.xandercat.pmdb.form.imdb;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;

/**
 * Form for IMDB searches. Hash code for this form hashes on user criteria excluding page number.
 * By excluding page number, search hashes can be compared to determine if search criteria
 * has changed.
 * 
 * Only the title, year, and page are meant to be manipulated by the user.  linkMovieId,
 * linkImdbId, and linkAll flag are for use internally for link processing.
 * 
 * @author Scott Arnold
 */
@Validated
public class SearchForm {

	@NotBlank
	private String title;
	
	private String year;
	
	@Range(min=1)
	private int page = 1;
	
	private String linkMovieId;
	private String linkImdbId;
	private boolean linkAll;
	
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
	public String getLinkMovieId() {
		return linkMovieId;
	}
	public void setLinkMovieId(String linkMovieId) {
		this.linkMovieId = linkMovieId;
	}
	public String getLinkImdbId() {
		return linkImdbId;
	}
	public void setLinkImdbId(String linkImdbId) {
		this.linkImdbId = linkImdbId;
	}
	public boolean isLinkAll() {
		return linkAll;
	}
	public void setLinkAll(boolean linkAll) {
		this.linkAll = linkAll;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((year == null) ? 0 : year.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchForm other = (SearchForm) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
}
