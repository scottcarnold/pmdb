package org.xandercat.pmdb.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="MovieCollection")
public class MovieCollection {

	@DynamoDBHashKey
	private String id;
	@DynamoDBAttribute
	private String name;
	@DynamoDBAttribute
	private String owner;
	@DynamoDBAttribute
	private boolean cloud;
	private boolean editable;  // this is here as a convenience and will be dependent on who the user is
	private boolean owned;     // this is here as a convenience and will be dependent on who the user is
	
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
	public void setOwner(String owner, String callingUsername) {
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
}
