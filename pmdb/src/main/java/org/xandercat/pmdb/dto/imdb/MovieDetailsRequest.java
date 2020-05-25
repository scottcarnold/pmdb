package org.xandercat.pmdb.dto.imdb;

import javax.ws.rs.QueryParam;

public class MovieDetailsRequest {

	@QueryParam("r")
	private ResponseType responseType;
	@QueryParam("i")
	private String imdbId;
	
	public MovieDetailsRequest(String imdbId) {
		this.imdbId = imdbId;
	}
	public ResponseType getResponseType() {
		return responseType;
	}
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
	public String getImdbId() {
		return imdbId;
	}
	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}
}
