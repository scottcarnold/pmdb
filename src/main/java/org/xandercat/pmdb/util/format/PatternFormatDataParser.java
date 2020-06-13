package org.xandercat.pmdb.util.format;

import java.text.Format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DataParser using patterns and format instances.
 * 
 * @author Scott Arnold
 *
 * @param <T>
 */
public class PatternFormatDataParser<T> implements DataParser<T> {

	private class PatternFormat {
		private Pattern pattern;
		private Format format;
		public PatternFormat(String regexPattern, Format format) {
			if (regexPattern != null) {
				this.pattern = Pattern.compile(regexPattern);
			}
			this.format = format;
		}
	}
	private List<PatternFormat> patternFormats = new ArrayList<PatternFormat>();
	
	public void addPattern(String regexPattern, Format parser) {
		patternFormats.add(new PatternFormat(regexPattern, parser));
	}

	public T parse(String s) {
		if (FormatUtil.isBlank(s)) {
			return null;
		}
		for (PatternFormat patternFormat : patternFormats) {
			if (patternFormat.pattern == null || patternFormat.pattern.matcher(s).matches()) {
				try {
					return (T) patternFormat.format.parseObject(s);
				} catch (ParseException e) {
				}
			}
		}
		return null;
	}
}
