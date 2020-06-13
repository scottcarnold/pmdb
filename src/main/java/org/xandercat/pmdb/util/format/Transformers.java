package org.xandercat.pmdb.util.format;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.service.ImdbAttribute;
import org.xandercat.pmdb.dto.FormattedMovie;

/**
 * Provides a means to auto-detect data types for movie attributes in order to improve the display and sorting
 * of those attributes when included in the movie table.
 * 
 * @author Scott Arnold
 */
public class Transformers {

	public static Set<FormattedMovie> getFormattedMovies(Set<Movie> movies, List<String> attributeNames) {
		List<DataTransformer<?>> dataTransformers = buildTransformers();
		
		// test the data
		Map<String, DataTransformerSelector> selectors = attributeNames.stream()
				.collect(Collectors.toMap(attributeName -> attributeName, attributeName -> new DataTransformerSelector(attributeName, dataTransformers)));
		movies.stream()
				.forEach(movie -> attributeNames.stream()
				.forEach(attributeName -> selectors.get(attributeName).test(movie.getAttribute(attributeName))));
		
		// build the transformer map
		Map<String, DataTransformer<?>> transformerMap = selectors.entrySet().stream()
				.filter(entry -> entry.getValue().getDataTransformer().isPresent())
				.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getDataTransformer().get()));
		
		// transformer customizations
		// remove transformer for IMDB Year attribute -- we don't want grouping for year and it doesn't need a transformer
		transformerMap.remove(ImdbAttribute.YEAR.getKey());
		
		// return the wrapped movies
		return movies.stream()
				.map(movie -> new FormattedMovie(movie, transformerMap)).collect(Collectors.toSet());
	}

	private static List<DataTransformer<?>> buildTransformers() {
		List<DataTransformer<?>> dataTransformers = new ArrayList<DataTransformer<?>>();
		dataTransformers.add(dateTransformer("MM/dd/yyyy", "yyyyMMdd",
				"dd MMM yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "MM-dd-yyyy", "yyyy-MM-dd", "M/d/yyyyy"));
		dataTransformers.add(doubleTransformer());
		dataTransformers.add(longTransformer());
		return dataTransformers;
	}
	
	private static DataTransformer<Date> dateTransformer(String displayPattern, String sortPattern, String... parsePatterns) {
		PatternFormatDataParser<Date> dateParser = new PatternFormatDataParser<Date>();
		for (String parsePattern : parsePatterns) {
			dateParser.addPattern(null, new SimpleDateFormat(parsePattern));
		}
		DateFormat dateSortValueFormatter = new SimpleDateFormat(sortPattern);
		DateFormat dateDisplayFormatter = new SimpleDateFormat(displayPattern); 
		return new DataTransformer<Date>("dateTransformer", dateParser, dateSortValueFormatter, dateDisplayFormatter);
	}
	
	private static DataTransformer<Long> longTransformer() {
		PatternFormatDataParser<Long> longParser = new PatternFormatDataParser<Long>();
		NumberFormat basicLongFormat = NumberFormat.getNumberInstance();
		basicLongFormat.setMaximumFractionDigits(0);
		longParser.addPattern(null, basicLongFormat);
		return new DataTransformer<Long>("longTransformer", longParser, null, basicLongFormat).defaultSortValue("0");
	}
	
	private static DataTransformer<Double> doubleTransformer() {
		PatternFormatDataParser<Double> doubleParser = new PatternFormatDataParser<Double>();
		NumberFormat doubleFormat = NumberFormat.getNumberInstance();
		doubleFormat.setMaximumFractionDigits(3);
		doubleParser.addPattern("^[0-9,]*\\.[0-9]*$", doubleFormat);
		return new DataTransformer<Double>("doubleTransformer", doubleParser, doubleFormat, null).defaultSortValue("0.0");
	}
}
