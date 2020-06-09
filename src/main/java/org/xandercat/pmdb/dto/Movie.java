package org.xandercat.pmdb.dto;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.annotation.Id;
import org.xandercat.pmdb.util.format.FormatUtil;
import org.xandercat.pmdb.util.format.MovieAttributesConverter;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

/**
 * Class to represent a movie and it's associated attributes. To support mapping of this object
 * to DynamoDB, the id is defined as a String
 * 
 * As a rule, attribute names must be stored in "Title Case", where the first letter of each word
 * is capitalized and all other letters are lower case. As the movie class serves as the 
 * primary holder of attributes, enforcing "Title Case" attribute names is handled within this class. 
 * 
 * @author Scott Arnold
 */
@DynamoDBTable(tableName="Movie")
public class Movie {

	@Id
	@DynamoDBHashKey
	private String id;
	
	@DynamoDBAttribute
	private String title;
	
	@DynamoDBTypeConverted(converter=MovieAttributesConverter.class)
	@DynamoDBAttribute
	private Map<String, String> attributes = new TreeMap<String, String>();
	
	@DynamoDBIndexHashKey(globalSecondaryIndexName = "idx_global_movie_collection_id")
	private String collectionId;
	
	public Movie() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void addAttribute(String key, String value) {
		attributes.put(FormatUtil.titleCase(key), value);
	}
	public String getAttribute(String key) {
		return attributes.get(FormatUtil.titleCase(key));
	}
	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}
	public void setAttributes(Map<String, String> attributes) {
		this.attributes.clear();
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			addAttribute(entry.getKey(), entry.getValue());
		}
	}
	public void removeAttribute(String key) {
		this.attributes.remove(FormatUtil.titleCase(key));
	}
	public String getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
