package org.xandercat.pmdb.util.format;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.xandercat.pmdb.dto.Movie;

public class DataFormattersTest {

	@Test
	public void testDataFormatters() {
		Map<String, DataFormatter> attrFormatters = new HashMap<String, DataFormatter>();
		attrFormatters.put("Votes", new LongFormatter());
		DataFormatters formatters = new DataFormatters();
		formatters.setGenericFormatter(new StringFormatter());
		formatters.setAttributeFormatters(attrFormatters);
		Movie movie = new Movie();
		movie.setId("id");
		movie.setTitle("title");
		movie.addAttribute("Votes", "1,250");
		assertEquals("1,250", formatters.displayValue(movie, "Votes"));
		assertEquals("1250", formatters.sortValue(movie, "Votes"));
	}

}
