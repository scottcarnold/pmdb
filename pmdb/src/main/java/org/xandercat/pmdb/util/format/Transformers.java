package org.xandercat.pmdb.util.format;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.FormattedMovie;

/**
 * Provides a means to auto-detect data types for movie attributes in order to improve the display and sorting
 * of those attributes when included in the movie table.
 * 
 * @author Scott Arnold
 */
public class Transformers {

	public static Set<FormattedMovie> getFormattedMovies(Set<Movie> movies, List<String> attributeNames) {
		Set<FormattedMovie> tmovies = new HashSet<FormattedMovie>();
		List<DataTransformer<?>> dataTransformers = buildTransformers();
		Map<String, DataTransformerSelector> selectors = new HashMap<String, DataTransformerSelector>();
		for (String attributeName : attributeNames) {
			DataTransformerSelector selector = new DataTransformerSelector(attributeName, dataTransformers);
			selectors.put(attributeName, selector);
		}
		for (Movie movie : movies) {
			for (String attributeName : attributeNames) {
				selectors.get(attributeName).test(movie.getAttributeValue(attributeName));
			}
		}
		Map<String, DataTransformer<?>> transformerMap = new HashMap<String, DataTransformer<?>>();
		for (Map.Entry<String, DataTransformerSelector> entry : selectors.entrySet()) {
			transformerMap.put(entry.getKey(), entry.getValue().getDataTransformer());
		}
		for (Movie movie : movies) {
			FormattedMovie tmovie = new FormattedMovie(movie, transformerMap);
			tmovies.add(tmovie);
		}
		return tmovies;
	}

	private static List<DataTransformer<?>> buildTransformers() {
		List<DataTransformer<?>> dataTransformers = new ArrayList<DataTransformer<?>>();
		dataTransformers.add(dateTransformer("MM/dd/yyyy", "yyyyMMdd",
				"dd MMM yyyy", "MM/dd/yyyy", "yyyy/MM/dd", "MM-dd-yyyy", "yyyy-MM-dd", "M/d/yyyyy"));
		return dataTransformers;
	}
	
	private static DataTransformer<Date> dateTransformer(String displayPattern, String sortPattern, String... parsePatterns) {
		DataParser<Date> dateParser = new DataParser<Date>();
		for (String parsePattern : parsePatterns) {
			dateParser.addPattern(null, new SimpleDateFormat(parsePattern));
		}
		DateFormat dateSortValueFormatter = new SimpleDateFormat(sortPattern);
		DateFormat dateDisplayFormatter = new SimpleDateFormat(displayPattern); 
		return new DataTransformer<Date>("dateTransformer", dateParser, dateSortValueFormatter, dateDisplayFormatter);
	}
}
