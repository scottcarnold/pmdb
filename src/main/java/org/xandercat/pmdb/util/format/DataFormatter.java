package org.xandercat.pmdb.util.format;

/**
 * Interface for data formatters that format values for display and sort in the view.
 * 
 * @author Scott Arnold
 */
public interface DataFormatter {

	/**
	 * Returns whether or not value appears to be valid for this data formatter.
	 *  
	 * @param value value to test
	 * 
	 * @return whether or not value appears valid for this formatter
	 */
	public boolean isAcceptable(String value);
	
	/**
	 * Returns the value as it should be displayed to the user.
	 * 
	 * @param value input value
	 * 
	 * @return value as it should be displayed to the user
	 */
	public String displayValue(String value);
	
	/**
	 * Returns the value as it should be prepared for natural sorting.
	 * 
	 * @param value input value
	 * 
	 * @return value that can be used for natural sorting
	 */
	public String sortValue(String value);
}
