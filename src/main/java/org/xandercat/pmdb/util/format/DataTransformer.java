package org.xandercat.pmdb.util.format;

import java.text.Format;

/**
 * Class for reformatting a String value for display and sort purposes within the view.  
 * 
 * If sort value formatter is not provided, the parsed value toString() method will be used for the sort value.
 * 
 * If display value formatter is not provided, the original String value will be used for the display value.
 * 
 * @author Scott Arnold
 *
 * @param <T>
 */
public class DataTransformer<T> {

	private String name;
	private DataParser<T> dataParser;
	private Format dataSortValueFormatter;
	private Format dataDisplayFormatter;
	private String defaultSortValue = "";
	
	public DataTransformer(String name, DataParser<T> dataParser, Format dataSortValueFormatter, Format dataDisplayFormatter) {
		this.name = name;
		this.dataParser = dataParser;
		this.dataSortValueFormatter = dataSortValueFormatter;
		this.dataDisplayFormatter = dataDisplayFormatter;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Set default sort value to use when no value is present.
	 * 
	 * @param defaultSortValue default sort value to use when no value is present
	 */
	public void setDefaultSortValue(String defaultSortValue) {
		this.defaultSortValue = defaultSortValue;
	}
	
	public boolean isParseable(String value) {
		if (FormatUtil.isBlank(value)) {
			return false;
		}
		return dataParser.parse(value) != null;
	}
	
	public String getSortValue(String value) {
		if (FormatUtil.isBlank(value)) {
			return defaultSortValue;
		}
		T iValue = dataParser.parse(value);
		if (dataSortValueFormatter != null) {
			return dataSortValueFormatter.format(iValue);
		} else {
			return (iValue == null)? null : iValue.toString();
		}
	}
	
	public String getDisplayValue(String value) {
		if (FormatUtil.isBlank(value)) {
			return "";
		}
		if (dataDisplayFormatter != null) {
			T iValue = dataParser.parse(value);
			return dataDisplayFormatter.format(iValue);
		} else {
			return value;
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		DataTransformer other = (DataTransformer) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	/**
	 * Builder method to set default sort value.
	 * 
	 * @param defaultSortValue default sort value when no value is present
	 * 
	 * @return this
	 */
	public DataTransformer<T> defaultSortValue(String defaultSortValue) {
		setDefaultSortValue(defaultSortValue);
		return this;
	}
}
