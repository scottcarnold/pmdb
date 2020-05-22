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
}
