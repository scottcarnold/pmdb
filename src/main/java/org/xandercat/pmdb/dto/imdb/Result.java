package org.xandercat.pmdb.dto.imdb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Class to represent the individual movies found from an IMDB search result.
 * 
 * @author Scott Arnold
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

	@XmlAttribute
	private String title;
	@XmlAttribute
	private String year;
	@XmlAttribute
	private String imdbID;
	@XmlAttribute
	private String type;
	@XmlAttribute
	private String poster;
	
	private boolean inCollection;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getImdbID() {
		return imdbID;
	}
	public void setImdbID(String imdbID) {
		this.imdbID = imdbID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public boolean isInCollection() {
		return inCollection;
	}
	public void setInCollection(boolean inCollection) {
		this.inCollection = inCollection;
	}
}
