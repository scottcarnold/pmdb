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
		this.attributes.put("Genre", movieDetails.getGenre());
		//TODO: Finish filling out the attributes
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
