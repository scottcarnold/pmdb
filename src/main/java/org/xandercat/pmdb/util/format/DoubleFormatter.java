package org.xandercat.pmdb.util.format;

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Data formatter for double values.
 * 
 * @author Scott Arnold
 */
public class DoubleFormatter extends AbstractDataFormatter<Double> {

	private NumberFormat doubleFormat = NumberFormat.getNumberInstance();
	
	public DoubleFormatter(int maximumFractionDigits) {
		doubleFormat.setMaximumFractionDigits(maximumFractionDigits);
	}
	
	@Override
	protected Double parse(String value) throws ParseException {
		return doubleFormat.parse(value).doubleValue();
	}

	@Override
	protected String getDisplayValue(Double value) {
		return doubleFormat.format(value.doubleValue());
	}

	@Override
	protected String getSortValue(Double value) {
		return doubleFormat.format(value.doubleValue());
	}

}
