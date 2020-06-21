package org.xandercat.pmdb.util.format;

import java.text.ParseException;

/**
 * Data formatter for String values.  This formatter leaves display of
 * String as is but uses lower case of string for sorting.
 * 
 * @author Scott Arnold
 */
public class StringFormatter extends AbstractDataFormatter<String> {

	@Override
	protected String parse(String value) throws ParseException {
		return value;
	}

	@Override
	protected String getDisplayValue(String value) {
		return value;
	}

	@Override
	protected String getSortValue(String value) {
		return value.toLowerCase();
	}
}
