package org.xandercat.pmdb.dto;

/**
 * Class to represent the permission a user has for a movie collection.
 * 
 * @author Scott Arnold
 */
public class CollectionPermission {

	private String collectionId;
	private String username;
	private boolean allowEdit;
	private boolean accepted;
	
	public String getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(String collectionId) {
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
	public boolean isAccepted() {
		return accepted;
	}
	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}
