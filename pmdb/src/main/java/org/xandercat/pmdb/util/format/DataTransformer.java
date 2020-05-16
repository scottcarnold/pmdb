package org.xandercat.pmdb.util.format;

import java.text.Format;

import org.thymeleaf.util.StringUtils;

public class DataTransformer<T> {

	private String name;
	private DataParser<T> dataParser;
	private Format dataSortValueFormatter;
	private Format dataDisplayFormatter;
	
	public DataTransformer(String name, DataParser<T> dataParser, Format dataSortValueFormatter, Format dataDisplayFormatter) {
		this.name = name;
		this.dataParser = dataParser;
		this.dataSortValueFormatter = dataSortValueFormatter;
		this.dataDisplayFormatter = dataDisplayFormatter;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isParseable(String value) {
		if (StringUtils.isEmptyOrWhitespace(value)) {
			return false;
		}
		return dataParser.parse(value) != null;
	}
	
	public String getSortValue(String value) {
		if (StringUtils.isEmptyOrWhitespace(value)) {
			return "";
		}
		T iValue = dataParser.parse(value);
		return dataSortValueFormatter.format(iValue);
	}
	
	public String getDisplayValue(String value) {
		if (StringUtils.isEmptyOrWhitespace(value)) {
			return "";
		}
		T iValue = dataParser.parse(value);
		return dataDisplayFormatter.format(iValue);
	}
}
