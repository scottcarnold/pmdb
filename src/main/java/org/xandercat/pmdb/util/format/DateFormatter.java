package org.xandercat.pmdb.util.format;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data formatter for dates (less time).
 * 
 * @author Scott Arnold
 */
public class DateFormatter extends AbstractDataFormatter<Date> {

	private List<DateFormat> dateParsers = new ArrayList<DateFormat>();
	private DateFormat displayFormat = new SimpleDateFormat("MM/dd/yyyy");
	private DateFormat sortFormat = new SimpleDateFormat("yyyyMMdd");
	
	@Override
	protected Date parse(String value) throws ParseException {
		for (DateFormat dateFormat : dateParsers) {
			try {
				return dateFormat.parse(value);
			} catch (ParseException e) {
			}
		}
		throw new ParseException("Unable to parse \"" + value + "\" to a date.", 0);
	}

	@Override
	protected String getDisplayValue(Date value) {
		return displayFormat.format(value);
	}

	@Override
	protected String getSortValue(Date value) {
		return sortFormat.format(value);
	}

	public DateFormatter parseFormat(String parseFormat) {
		dateParsers.add(new SimpleDateFormat(parseFormat));
		return this;
	}

	public DateFormatter parseFormats(String... parseFormats) {
		for (String parseFormat : parseFormats) {
			dateParsers.add(new SimpleDateFormat(parseFormat));
		}
		return this;
	}
	
	public DateFormatter displayFormat(String displayFormat) {
		this.displayFormat = new SimpleDateFormat(displayFormat);
		return this;
	}
}
