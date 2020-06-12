package org.xandercat.pmdb.dto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;
import org.xandercat.pmdb.util.DoubleStatistics;
import org.xandercat.pmdb.util.LongStatistics;
import org.xandercat.pmdb.util.format.FormatUtil;

/**
 * Class for providing generic statistics about a collection of movies.
 * 
 * @author Scott Arnold
 */
public class MovieStatistics {

	private Collection<Movie> movies;
	
	public MovieStatistics(Collection<Movie> movies) {
		this.movies = movies;
	}
	
	private Double getDoubleAttribute(Movie movie, String attributeKey) {
		String value = movie.getAttribute(attributeKey);
		if (StringUtils.hasText(value)) {
			try {
				return Double.valueOf(value);
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	private Long getLongAttribute(Movie movie, String attributeKey) {
		String value = movie.getAttribute(attributeKey);
		if (StringUtils.hasText(value)) {
			try {
				return Long.valueOf(value);
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	private Long getLenientLongAttribute(Movie movie, String attributeKey) {
		String value = movie.getAttribute(attributeKey);
		if (StringUtils.hasText(value)) {
			try {
				return Long.valueOf(FormatUtil.formatAsNumber(value));
			} catch (Exception e) {
			}
		}
		return null;		
	}
	
	/**
	 * Returns statistics for a movie attribute that should contain double values.
	 * Statistics will only be present if there is at least 1 value to calculate statistics on.
	 * 
	 * @param attributeKey  key for movie attribute to run statistics on
	 * 
	 * @return statistics for the attribute
	 */
	public Optional<DoubleStatistics> getDoubleStatistics(String attributeKey) {
		List<Double> doubles = movies.stream()
				.map(movie -> getDoubleAttribute(movie, attributeKey))
				.filter(value -> value != null)
				.collect(Collectors.toList());
		return (doubles.size() > 0)? Optional.of(new DoubleStatistics(doubles)) : Optional.empty();
	}
	
	/**
	 * Returns statistics for a movie attribute that should contain long values.
	 * Statistics will only be present if there is at least 1 value to calculate statistics on.
	 * 
	 * @param attributeKey  key for movie attribute to run statistics on
	 * 
	 * @return statistics for the attribute
	 */
	public Optional<LongStatistics> getLongStatistics(String attributeKey) {
		List<Long> longs = movies.stream()
				.map(movie -> getLongAttribute(movie, attributeKey))
				.filter(value -> value != null)
				.collect(Collectors.toList());
		return (longs.size() > 0)? Optional.of(new LongStatistics(longs)) : Optional.empty();
	}
	
	/**
	 * Returns statistics for a movie attribute that should contain long values with lenient parsing.
	 * Lenient parsing throws away extraneous commas, currency symbols, or other non-numeric clutter in 
	 * the attribute value.  Statistics will only be present if there is at least 1 value to calculate statistics on.
	 * 
	 * @param attributeKey  key for movie attribute to run statistics on
	 * 
	 * @return statistics for the attribute
	 */
	public Optional<LongStatistics> getLenientLongStatistics(String attributeKey) {
		List<Long> longs = movies.stream()
				.map(movie -> getLenientLongAttribute(movie, attributeKey))
				.filter(value -> value != null)
				.collect(Collectors.toList());
		return (longs.size() > 0)? Optional.of(new LongStatistics(longs)) : Optional.empty();
	}
}
