package org.xandercat.pmdb.form.collection;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.xandercat.pmdb.dto.MovieCollection;

/**
 * Form for updating movie collection details.
 * 
 * @author Scott Arnold
 */
@Validated
public class CollectionForm {

	private String id;
	
	@NotBlank
	@Length(max=100)
	private String name;
	
	private boolean cloud;
	
	public CollectionForm() {
	}
	public CollectionForm(MovieCollection movieCollection) {
		this.id = movieCollection.getId();
		this.name = movieCollection.getName();
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isCloud() {
		return cloud;
	}
	public void setCloud(boolean cloud) {
		this.cloud = cloud;
	}
}
