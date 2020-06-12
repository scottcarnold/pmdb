package org.xandercat.pmdb.dto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;
import org.xandercat.pmdb.util.DoubleStatistics;

/**
 * Class for providing generic statistics about a collection of movies.
 * 
 * @author Scott Arnold
 */
public class MovieStatistics {

	private Collection<Movie> movies;

	private Double getDoubleAttribute(Movie movie, String attributeKey) {
		String value = movie.getAttribute(attributeKey);
		if (StringUtils.hasText(value)) {
			try {
				return Double.parseDouble(value);
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	public MovieStatistics(Collection<Movie> movies) {
		this.movies = movies;
	}
	
	/**
	 * Returns statistics for a movie attribute that should contain double values.
	 * Statistics will only be present if there is at least 1 value to calculate statistics on.
	 * 
	 * @param attributeKey  key for movie attribute to run statistics on
	 * 
	 * @return statistics for the attribute
	 */
	public Optional<DoubleStatistics> getDoubleStatsitics(String attributeKey) {
		List<Double> doubles = movies.stream()
				.map(movie -> getDoubleAttribute(movie, attributeKey))
				.filter(value -> value != null)
				.collect(Collectors.toList());
		return (doubles.size() > 0)? Optional.of(new DoubleStatistics(doubles)) : Optional.empty();
	}
}
