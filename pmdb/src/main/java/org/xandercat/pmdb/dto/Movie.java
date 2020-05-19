package org.xandercat.pmdb.dto;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.thymeleaf.util.StringUtils;
import org.xandercat.pmdb.dto.imdb.MovieDetails;
import org.xandercat.pmdb.service.ImdbSearchService;
import org.xandercat.pmdb.util.format.FormatUtil;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Class to represent a movie and it's associated attributes. To support mapping of this object
 * to DynamoDB, the id is defined as a String; getIntId() and setIntId() methods have been
 * added to continue to provide the convenience of using an int where preferable.
 * 
 * As a rule, attribute names must be stored in "Title Case", where the first letter of each word
 * is capitalized and all other letters are lower case. As the movie class serves as the 
 * primary holder of attributes, enforcing "Title Case" attribute names is handled within this class. 
 * 
 * @author Scott Arnold
 */
@DynamoDBTable(tableName="Movie")
public class Movie {

	private static final int MAX_ATTRIBUTE_VALUE_LENGTH = 400;
	private static final String IMDB_URL_BASE = "https://www.imdb.com/title/";
	
	@DynamoDBHashKey
	private String id;
	
	@DynamoDBAttribute
	private String title;
	
	@DynamoDBAttribute
	private Map<String, String> attributes = new TreeMap<String, String>();
	
	@DynamoDBAttribute
	private String collectionId;
	
	public Movie() {
	}
	public Movie(MovieDetails movieDetails, String collectionId) {
		this.title = movieDetails.getTitle();
		this.collectionId = collectionId;
		setAttribute("year", movieDetails.getYear());
		setAttribute("genre", movieDetails.getGenre());
		setAttribute("rated", movieDetails.getRated());
		setAttribute("plot", movieDetails.getPlot());
		setAttribute("actors", movieDetails.getActors());
		setAttribute("director", movieDetails.getDirector());
		setAttribute("awards", movieDetails.getAwards());
		setAttribute(ImdbSearchService.IMDB_ID_KEY, movieDetails.getImdbId());
		if (!StringUtils.isEmptyOrWhitespace(movieDetails.getImdbId())) {
			setAttribute("imdb url", IMDB_URL_BASE + movieDetails.getImdbId());
		}
		setAttribute("imdb rating", movieDetails.getImdbRating());
		setAttribute("imdb votes", movieDetails.getImdbVotes());
		setAttribute("language", movieDetails.getLanguage());
		setAttribute("metascore", movieDetails.getMetascore());		
		setAttribute("poster", movieDetails.getPoster());		
		setAttribute("released", movieDetails.getReleased());
		setAttribute("runtime", movieDetails.getRuntime());
		setAttribute("type", movieDetails.getType());
		setAttribute("country", movieDetails.getCountry());
		
	}
	private void setAttribute(String name, String value) {
		if (!StringUtils.isEmptyOrWhitespace(value)) {
			if (value.length() > MAX_ATTRIBUTE_VALUE_LENGTH) {
				value = value.substring(0, MAX_ATTRIBUTE_VALUE_LENGTH);
			}
			addAttribute(name, value);
		}
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
	public String getCollectionId() {
		return collectionId;
	}
	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}
	public String getAttributeValue(String key) {
		return this.attributes.get(key);
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
