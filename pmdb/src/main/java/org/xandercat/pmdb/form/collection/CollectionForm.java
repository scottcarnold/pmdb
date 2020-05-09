package org.xandercat.pmdb.form.collection;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.xandercat.pmdb.dto.MovieCollection;

@Validated
public class CollectionForm {

	private int id;
	
	@NotBlank
	private String name;
	
	public CollectionForm() {
	}
	public CollectionForm(MovieCollection movieCollection) {
		this.id = movieCollection.getId();
		this.name = movieCollection.getName();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
