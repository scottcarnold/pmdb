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

	protected abstract T parse(String value) throws ParseException;
	
	protected abstract String getDisplayValue(T value);
	
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
