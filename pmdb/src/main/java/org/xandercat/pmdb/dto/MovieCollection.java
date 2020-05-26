package org.xandercat.pmdb.dto;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="MovieCollection")
public class MovieCollection {

	@Id
	@DynamoDBHashKey
	private String id;
	@DynamoDBAttribute
	private String name;
	@DynamoDBAttribute
	private String owner;
	@DynamoDBAttribute
	private boolean cloud;
	private boolean editable;  // this is here as a convenience and will be dependent on who the user is; why does spring-data-dynamodb map this when it's not annotated?
	private boolean owned;     // this is here as a convenience and will be dependent on who the user is; why does spring-data-dynamodb map this when it's not annotated? 
	
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
	public String getOwner() {
		return owner;
	}
	/**
	 * Ideally call setOwnerAndOwned(String, String) so that the owned flag is set as well, but this method
	 * needs to be here for mapping purposes.
	 * 
	 * @param owner owner username
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public void setOwnerAndOwned(String owner, String callingUsername) {
		this.owner = owner;
		if (callingUsername.equals(owner)) {
			this.owned = true;
		}
	}
	public boolean isCloud() {
		return cloud;
	}
	public void setCloud(boolean cloud) {
		this.cloud = cloud;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public boolean isOwned() {
		return owned;
	}
	/**
	 * No-op method needed for DynamoDB mapping.  Owner flag should
	 * be set by calling setOwnerAndOwned(String, String).
	 * 
	 * @param owned
	 */
	public void setOwned(boolean owned) {
	}
}
