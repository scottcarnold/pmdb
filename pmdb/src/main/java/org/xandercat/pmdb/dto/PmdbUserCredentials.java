package org.xandercat.pmdb.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="User")
public class PmdbUserCredentials {

	@DynamoDBHashKey
	private String username;
	@DynamoDBAttribute
	private byte[] password;
	
	public PmdbUserCredentials() {
	}
	public PmdbUserCredentials(String username, byte[] password) {
		this.username = username;
		this.password = password;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public byte[] getPassword() {
		return password;
	}
	public void setPassword(byte[] password) {
		this.password = password;
	}

}
