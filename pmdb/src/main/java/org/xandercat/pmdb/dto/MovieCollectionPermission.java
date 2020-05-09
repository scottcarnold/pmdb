package org.xandercat.pmdb.dto;

public class MovieCollectionPermission {

	private int collectionId;
	private String username;
	private boolean allowEdit;
	
	public int getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public boolean isAllowEdit() {
		return allowEdit;
	}
	public void setAllowEdit(boolean allowEdit) {
		this.allowEdit = allowEdit;
	}

	
}
