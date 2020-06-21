package org.xandercat.pmdb.util.format;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Data formatter for Long values.
 * 
 * @author Scott Arnold
 */
public class LongFormatter extends AbstractDataFormatter<Long> {

	private NumberFormat basicLongFormat = NumberFormat.getNumberInstance();
	private char decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
	
	public LongFormatter() {
		basicLongFormat.setMaximumFractionDigits(0);
	}
	
	@Override
	protected Long parse(String value) throws ParseException {
		int decimalSeparatorIndex = value.indexOf(decimalSeparator);
		if (decimalSeparatorIndex >= 0) {
			// this helps ensure any floating point formatter will take precedence for numbers with decimals
			throw new ParseException("LongFormatter will not parse number with a decimal separator: " + value, decimalSeparatorIndex);
		}
		return basicLongFormat.parse(value).longValue();
	}

	@Override
	protected String getDisplayValue(Long value) {
		return basicLongFormat.format(value.longValue());
	}

	@Override
	protected String getSortValue(Long value) {
		return value.toString();
	}
}
