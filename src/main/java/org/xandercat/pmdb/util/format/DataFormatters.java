package org.xandercat.pmdb.util.format;

import java.util.HashMap;
import java.util.Map;

import org.xandercat.pmdb.dto.Movie;

/**
 * Formatting class for formatting display and sort values for movie attributes.
 * 
 * @author Scott Arnold
 */
public class DataFormatters {

	private Map<String, DataFormatter> attributeFormatters = new HashMap<String, DataFormatter>();
	private DataFormatter genericFormatter;

	/**
	 * Sets the generic formatter that will be used to format values when no explicit formatter for an attribute has been set.
	 * 
	 * @param genericFormatter generic formatter
	 */
	public void setGenericFormatter(DataFormatter genericFormatter) {
		this.genericFormatter = genericFormatter;
	}
	
	/**
	 * Sets the formatters to be used for movie attributes.
	 * 
	 * @param attributeFormatters formatters for movie attributes (key is attribute name, value is formatter)
	 */
	public void setAttributeFormatters(Map<String, DataFormatter> attributeFormatters) {
		if (attributeFormatters != null) {
			this.attributeFormatters = attributeFormatters;
		} else {
			this.attributeFormatters.clear();
		}
	}
	
	/**
	 * Returns the attribute formatters for movie attributes.
	 * 
	 * @return attribute formatters for movie attributes
	 */
	public Map<String, DataFormatter> getAttributeFormatters() {
		return attributeFormatters;
	}
	
	/**
	 * Adds an attribute formatter for the given attribute name.
	 * 
	 * @param attributeName        name of attribute
	 * @param attributeFormatter   formatter for attribute
	 */
	public void addAttributeFormatter(String attributeName, DataFormatter attributeFormatter) {
		attributeFormatters.put(attributeName, attributeFormatter);
	}
	
	/**
	 * Returns the display value for the attribute of given name on the given movie.
	 * 
	 * @param movie          movie to get display value from
	 * @param attributeName  attribute name to get display value for
	 * 
	 * @return display value for attribute
	 */
	public String displayValue(Movie movie, String attributeName) {
		String attributeValue = movie.getAttribute(attributeName);
		DataFormatter attributeFormatter = attributeFormatters.get(attributeName);
		if (attributeFormatter == null) {
			attributeFormatter = genericFormatter;
		}
		if (attributeFormatter == null) {
			return attributeValue;
		} else {
			return attributeFormatter.displayValue(attributeValue);
		}
	}
	
	/**
	 * Returns the sort value for the attribute of given name on the given movie.
	 * 
	 * @param movie          movie to get sort value from
	 * @param attributeName  attribute name to get sort value for
	 * 
	 * @return sort value for attribute
	 */
	public String sortValue(Movie movie, String attributeName) {
		String attributeValue = movie.getAttribute(attributeName);
		DataFormatter attributeFormatter = attributeFormatters.get(attributeName);
		if (attributeFormatter == null) {
			attributeFormatter = genericFormatter;
		}
		if (attributeFormatter == null) {
			return attributeValue;
		} else {
			return attributeFormatter.sortValue(attributeValue);
		}		
	}
}
