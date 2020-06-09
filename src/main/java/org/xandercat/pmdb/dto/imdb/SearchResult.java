package org.xandercat.pmdb.dto.imdb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class to represent an IMDB search result.
 * 
 * @author Scott Arnold
 */
@XmlRootElement(name="root")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchResult {

	@XmlElement(name="result")
	private List<Result> results;
	@XmlAttribute
	private String totalResults;
	@XmlAttribute
	private String response;
	
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
	public String getTotalResults() {
		return totalResults;
	}
	public void setTotalResults(String totalResults) {
		this.totalResults = totalResults;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
}
