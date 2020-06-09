package org.xandercat.pmdb.dto.imdb;

/**
 * Enum to represent response types supported by the IMDB web service.
 * 
 * @author Scott Arnold
 */
public enum ResponseType {
	
	XML("xml"), JSON("json");
	
	private String paramValue;
	
	private ResponseType(String paramValue) {
		this.paramValue = paramValue;
	}
	
	public String toString() {
		return paramValue;
	}
}
