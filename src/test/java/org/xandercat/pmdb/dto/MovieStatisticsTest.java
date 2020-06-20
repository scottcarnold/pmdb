package org.xandercat.pmdb.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xandercat.pmdb.util.DoubleStatistics;
import org.xandercat.pmdb.util.LongStatistics;
import org.xandercat.pmdb.util.WordStatistics;

public class MovieStatisticsTest {

	private List<Movie> movies;
	
	private class MovieBuilder {
		
		private Movie movie = new Movie();
		
		public MovieBuilder title(String title) {
			movie.setTitle(title);
			return this;
		}
		
		public MovieBuilder attr(String key, String value) {
			movie.addAttribute(key, value);
			return this;
		}

		public Movie build() {
			return movie;
		}
	}
	
	@BeforeEach
	public void beforeEach() {
		movies = new ArrayList<Movie>();
		movies.add(new MovieBuilder().title("One").attr("Votes", "1,400").attr("Rating", "5.5").attr("Genre", "Drama").build());
		movies.add(new MovieBuilder().title("Two").attr("Votes", "2,600").attr("Rating", "7.5").attr("Genre", "Action").build());
		movies.add(new MovieBuilder().title("Three").attr("Votes", "10,100").attr("Rating", "8.0").attr("Genre", "Action").attr("AwardCount", "2").build());
	}
	
	@Test
	public void testEmptyStats() {
		MovieStatistics stats = new MovieStatistics(Collections.emptyList());
		Optional<DoubleStatistics> doubleStats = stats.getDoubleStatistics("Any");
		Optional<LongStatistics> longStats = stats.getLongStatistics("Any");
		Optional<WordStatistics> wordStats = stats.getWordStatistics("Any");
		assertFalse(doubleStats.isPresent());
		assertFalse(longStats.isPresent());
		assertFalse(wordStats.isPresent());
	}

	@Test
	public void testLenientLongStats() {
		MovieStatistics stats = new MovieStatistics(movies);
		Optional<LongStatistics> longStats = stats.getLenientLongStatistics("Votes");
		assertTrue(longStats.isPresent());
		assertEquals(10100, longStats.get().getMax());
	}
	
	@Test
	public void testLongStats() {
		MovieStatistics stats = new MovieStatistics(movies);
		Optional<LongStatistics> longStats = stats.getLongStatistics("AwardCount");
		assertTrue(longStats.isPresent());
		assertEquals(2, longStats.get().getMax());
	}
	
	@Test
	public void testDoubleStats() {
		MovieStatistics stats = new MovieStatistics(movies);
		Optional<DoubleStatistics> dblStats = stats.getDoubleStatistics("Rating");
		assertTrue(dblStats.isPresent());
		assertEquals(8.0, dblStats.get().getMax(), 0.001);
	}
	
	@Test
	public void testWordStats() {
		MovieStatistics stats = new MovieStatistics(movies);
		Optional<WordStatistics> wordStats = stats.getWordStatistics("Genre");
		assertTrue(wordStats.isPresent());
		assertEquals(2, wordStats.get().getWordCounts().size());
	}
}
