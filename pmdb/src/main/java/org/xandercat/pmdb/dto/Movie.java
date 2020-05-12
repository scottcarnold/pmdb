package org.xandercat.pmdb.dto;

import java.util.Map;
import java.util.TreeMap;

import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.util.CIString;

public class Movie {

	private int id;
	private String title;
	private Map<CIString, String> attributes = new TreeMap<CIString, String>();
	private int collectionId;
	
	public Movie() {
	}
	public Movie(MovieDetails movieDetails, int collectionId) {
		this.title = movieDetails.getTitle();
		this.collectionId = collectionId;
		this.attributes.put(new CIString("Year"), movieDetails.getYear());
		this.attributes.put(new CIString("Genre"), movieDetails.getGenre());
		this.attributes.put(new CIString("Rated"), movieDetails.getRated());
		this.attributes.put(new CIString("Plot"), movieDetails.getPlot());
		this.attributes.put(new CIString("Actors"), movieDetails.getActors());
		this.attributes.put(new CIString("Director"), movieDetails.getDirector());
		this.attributes.put(new CIString("Awards"), movieDetails.getAwards());
		this.attributes.put(new CIString("IMDB ID"), movieDetails.getImdbId());
		this.attributes.put(new CIString("IMDB Rating"), movieDetails.getImdbRating());
		this.attributes.put(new CIString("IMDB Votes"), movieDetails.getImdbVotes());
		this.attributes.put(new CIString("Language"), movieDetails.getLanguage());
		this.attributes.put(new CIString("Metascore"), movieDetails.getMetascore());		
		this.attributes.put(new CIString("Poster"), movieDetails.getPoster());		
		this.attributes.put(new CIString("Released"), movieDetails.getReleased());
		this.attributes.put(new CIString("Runtime"), movieDetails.getRuntime());
		this.attributes.put(new CIString("Type"), movieDetails.getType());
		this.attributes.put(new CIString("Country"), movieDetails.getCountry());
		
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
}
