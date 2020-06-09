package org.xandercat.pmdb.dto.imdb;

import javax.ws.rs.QueryParam;

/**
 * Class to represent an IMDB search request.
 * 
 * @author Scott Arnold
 */
public class SearchRequest {

	@QueryParam("s")
	private String title;
	@QueryParam("y")
	private String year;
	@QueryParam("page")
	private int page;
	@QueryParam("r")
	private ResponseType responseType;
	
	public SearchRequest() {
	}
	public SearchRequest(String title, String year, int page) {
		this.title = title;
		this.year = year;
		this.page = page;
	}
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
	public ResponseType getResponseType() {
		return responseType;
	}
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
}
