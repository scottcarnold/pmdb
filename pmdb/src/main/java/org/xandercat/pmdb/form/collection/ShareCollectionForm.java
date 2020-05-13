package org.xandercat.pmdb.form.collection;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

@Validated
public class ShareCollectionForm {

	private int collectionId;
	@NotBlank
	private String usernameOrEmail;
	private boolean editable;

	public ShareCollectionForm() {
	}
	
	public ShareCollectionForm(int collectionId) {
		this.collectionId = collectionId;
	}
	
	public int getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
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
