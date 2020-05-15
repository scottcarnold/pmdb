package org.xandercat.pmdb.dto;

import java.util.Map;
import java.util.TreeMap;

import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.util.CIString;

public class Movie {

	private static final int MAX_ATTRIBUTE_VALUE_LENGTH = 200;
	private static final String IMDB_URL_BASE = "https://www.imdb.com/title/";
	
	private int id;
	private String title;
	private Map<CIString, String> attributes = new TreeMap<CIString, String>();
	private int collectionId;
	
	public Movie() {
	}
	public Movie(MovieDetails movieDetails, int collectionId) {
		this.title = movieDetails.getTitle();
		this.collectionId = collectionId;
		setAttribute("Year", movieDetails.getYear());
		setAttribute("Genre", movieDetails.getGenre());
		setAttribute("Rated", movieDetails.getRated());
		setAttribute("Plot", movieDetails.getPlot());
		setAttribute("Actors", movieDetails.getActors());
		setAttribute("Director", movieDetails.getDirector());
		setAttribute("Awards", movieDetails.getAwards());
		if (!StringUtils.isEmptyOrWhitespace(movieDetails.getImdbId())) {
			setAttribute("IMDB URL", IMDB_URL_BASE + movieDetails.getImdbId());
		}
		setAttribute("IMDB Rating", movieDetails.getImdbRating());
		setAttribute("IMDB Votes", movieDetails.getImdbVotes());
		setAttribute("Language", movieDetails.getLanguage());
		setAttribute("Metascore", movieDetails.getMetascore());		
		setAttribute("Poster", movieDetails.getPoster());		
		setAttribute("Released", movieDetails.getReleased());
		setAttribute("Runtime", movieDetails.getRuntime());
		setAttribute("Type", movieDetails.getType());
		setAttribute("Country", movieDetails.getCountry());
		
	}
	private void setAttribute(String name, String value) {
		if (!StringUtils.isEmptyOrWhitespace(value)) {
			if (value.length() > MAX_ATTRIBUTE_VALUE_LENGTH) {
				value = value.substring(0, MAX_ATTRIBUTE_VALUE_LENGTH);
			}
			this.attributes.put(new CIString(name), value);
		}
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Map<CIString, String> getAttributes() {
		return attributes;
	}
	// forcing any setter to provide a TreeMap to enforce sorted order
	public void setAttributes(TreeMap<CIString, String> attributes) {
		this.attributes = attributes;
	}
	public int getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	public String getAttributeValue(String key) {
		return this.attributes.get(new CIString(key));
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Movie other = (Movie) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
