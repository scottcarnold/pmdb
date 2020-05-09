package org.xandercat.pmdb.form.movie;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.xandercat.pmdb.dto.Movie;

@Validated
public class MovieForm {

	private int id;
	
	@NotBlank
	private String title;
	
	private int collectionId;
	
	public MovieForm() {
	}
	
	public MovieForm(Movie movie) {
		this.id = movie.getId();
		this.title = movie.getTitle();
		this.collectionId = movie.getCollectionId();
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

	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

}
