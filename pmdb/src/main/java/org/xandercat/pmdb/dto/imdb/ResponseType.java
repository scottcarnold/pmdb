package org.xandercat.pmdb.dto.imdb;

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
