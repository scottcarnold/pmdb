package org.xandercat.pmdb.util.format;

import java.text.ParseException;

/**
 * Abstract class for formatting values for display and sort in the view.
 * 
 * @author Scott Arnold
 *
 * @param <T> native type for value being formatted
 */
public abstract class AbstractDataFormatter<T> implements DataFormatter {

	private String defaultDisplayValue;
	private String defaultSortValue;
	private boolean displayFormattedValue = true;

	/**
	 * Parse the provided string value to the native data type.  If the value
	 * cannot be parsed, a ParseException should be thrown.  This method does
	 * not need to concern itself with blank values; it will only be called
	 * for non-blank values.
	 * 
	 * @param value value to parse
	 * 
	 * @return parsed value
	 * @throws ParseException if value cannot be parsed
	 */
	protected abstract T parse(String value) throws ParseException;
	
	/**
	 * Returns value suitable for display to a user.  This method does not
	 * need to concern itself with blank values; it will only be called for 
	 * non-blank values.
	 * 
	 * @param value value in native type
	 * 
	 * @return value formatted for display to user
	 */
	protected abstract String getDisplayValue(T value);
	
	/**
	 * Returns value suitable for natural String sorting.
	 * 
	 * @param value value in native type
	 * 
	 * @return value formatted for string sorting
	 */
	protected abstract String getSortValue(T value);
	
	@Override
	public boolean isAcceptable(String value) {
		if (FormatUtil.isBlank(value)) {
			return true;
		}
		try {
			parse(value);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	@Override
	public String displayValue(String value) {
		if (FormatUtil.isBlank(value)) {
			return (defaultDisplayValue != null)? defaultDisplayValue : value;
		}
		if (!displayFormattedValue) {
			return value;
		}
		try {
			T convertedValue = parse(value);
			return getDisplayValue(convertedValue);
		} catch (ParseException e) {
			return value;
		}
	}

	@Override
	public String sortValue(String value) {
		if (FormatUtil.isBlank(value)) {
			return (defaultSortValue != null)? defaultSortValue : value;
		}
		try {
			T convertedValue = parse(value);
			return getSortValue(convertedValue);
		} catch (ParseException e) {
			return value;
		}
	}
	
	public AbstractDataFormatter<T> defaultDisplayValue(String defaultDisplayValue) {
		this.defaultDisplayValue = defaultDisplayValue;
		return this;
	}
	
	public AbstractDataFormatter<T> defaultSortValue(String defaultSortValue) {
		this.defaultSortValue = defaultSortValue;
		return this;
	}
	
	public AbstractDataFormatter<T> displayFormattedValue(boolean displayFormattedValue) {
		this.displayFormattedValue = displayFormattedValue;
		return this;
	}
}
