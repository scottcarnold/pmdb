package org.xandercat.pmdb.dto;

import java.util.HashMap;
import java.util.Map;

import org.xandercat.pmdb.dto.imdb.MovieDetails;

public class Movie {

	private int id;
	private String title;
	private Map<String, String> attributes = new HashMap<String, String>();
	private int collectionId;
	
	public Movie() {
	}
	public Movie(MovieDetails movieDetails, int collectionId) {
		this.title = movieDetails.getTitle();
		this.collectionId = collectionId;
		this.attributes.put("Year", movieDetails.getYear());
		this.attributes.put("Genre", movieDetails.getGenre());
		this.attributes.put("Rated", movieDetails.getRated());
		this.attributes.put("Plot", movieDetails.getPlot());
		this.attributes.put("Actors", movieDetails.getActors());
		this.attributes.put("Director", movieDetails.getDirector());
		this.attributes.put("Awards", movieDetails.getAwards());
		this.attributes.put("IMDB ID", movieDetails.getImdbId());
		this.attributes.put("IMDB Rating", movieDetails.getImdbRating());
		this.attributes.put("IMDB Votes", movieDetails.getImdbVotes());
		this.attributes.put("Language", movieDetails.getLanguage());
		this.attributes.put("Metascore", movieDetails.getMetascore());		
		this.attributes.put("Poster", movieDetails.getPoster());		
		this.attributes.put("Released", movieDetails.getReleased());
		this.attributes.put("Runtime", movieDetails.getRuntime());
		this.attributes.put("Type", movieDetails.getType());
		this.attributes.put("Country", movieDetails.getCountry());
		
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
	public Map<String, String> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	public int getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
}
