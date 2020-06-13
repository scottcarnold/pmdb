package org.xandercat.pmdb.service;

/**
 * Enum to represent the various IMDB movie attributes.
 * 
 * @author Scott Arnold
 */
public enum ImdbAttribute {
	IMDB_ID("Imdb Id"), 
	YEAR("Year"), 
	GENRE("Genre"), 
	RATED("Rated"), 
	PLOT("Plot"), 
	ACTORS("Actors"), 
	DIRECTOR("Director"), 
	AWARDS("Awards"), 
	IMDB_URL("Imdb Url"), 
	IMDB_RATING("Imdb Rating"), 
	IMDB_VOTES("Imdb Votes"), 
	LANGUAGE("Language"), 
	METASCORE("Metascore"), 
	POSTER("Poster"), 
	RELEASED("Released"), 
	RUNTIME("Runtime"), 
	TYPE("Type"), 
	COUNTRY("Country");
	
	private String key;
	
	private ImdbAttribute(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
	public String toString() {
		return key;
	}
}
