package org.xandercat.pmdb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xandercat.pmdb.dto.Movie;

public class MovieTitleComparatorTest {
	
	private MovieTitleComparator mtc;
	private List<Movie> movies;
	
	private Movie movie(String id, String title) {
		Movie movie = new Movie();
		movie.setId(id);
		movie.setTitle(title);
		return movie;
	}
	
	@BeforeEach
	public void beforeEach() {
		mtc = new MovieTitleComparator();
		movies = new ArrayList<Movie>();
		movies.add(movie("2", "Born Again"));
		movies.add(movie("1", "avengers"));
		movies.add(movie("3", "Friday"));
		movies.add(movie("0", "Air America"));
	}
	
	@Test
	public void testSortByTitle() {
		Collections.sort(movies, mtc);
		for (int i=0; i<4; i++) {
			assertEquals(String.valueOf(i), movies.get(i).getId());
		}
	}
	
	@Test
	public void testSortWithNulls() {
		Movie nullsMovie = movie("0", null);
		Movie movie = movie("1", "Movie Title");
		assertTrue(mtc.compare(nullsMovie, movie) < 0);
		assertTrue(mtc.compare(movie, nullsMovie) > 0);
	}
}