package org.xandercat.pmdb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.xandercat.pmdb.dto.Movie;
import org.xandercat.pmdb.dto.MovieCollection;

public class ExcelPorterTest {

	@Test
	public void testImportExport() {
		ExcelPorter exporter = new ExcelPorter(ExcelPorter.Format.XLSX);
		MovieCollection movieCollection = new MovieCollection();
		movieCollection.setId("cId");
		movieCollection.setName("My Collection");
		Movie movie = new Movie();
		movie.setId("id");
		movie.setTitle("title");
		exporter.addSheet(movieCollection, Collections.singletonList(movie), Collections.emptyList());
		List<String> sheetNames = exporter.getSheetNames();
		assertNotNull(sheetNames);
		assertEquals(1, sheetNames.size());
		assertEquals("My Collection", sheetNames.get(0));
		List<Movie> movies = exporter.getMoviesForSheet("My Collection", Collections.singletonList("Title"));
		assertNotNull(movies);
		assertEquals(1, movies.size());
		assertEquals("title", movies.get(0).getTitle());
	}

}
