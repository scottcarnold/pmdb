package org.xandercat.pmdb.form.collection;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * Form for sharing a movie collection with another user.
 * 
 * @author Scott Arnold
 */
@Validated
public class ShareCollectionForm {

	private String collectionId;
	@NotBlank
	private String usernameOrEmail;
	private boolean editable;

	public ShareCollectionForm() {
	}
	
	public ShareCollectionForm(String collectionId) {
		this.collectionId = collectionId;
	}
	
	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getUsernameOrEmail() {
		return usernameOrEmail;
	}

	public void setUsernameOrEmail(String usernameOrEmail) {
		this.usernameOrEmail = usernameOrEmail;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

}
