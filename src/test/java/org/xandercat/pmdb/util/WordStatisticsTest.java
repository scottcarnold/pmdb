package org.xandercat.pmdb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class WordStatisticsTest {

	@Test
	public void testEmptyWordStatistics() {
		WordStatistics stats = new WordStatistics();
		assertNotNull(stats.getWordCounts());
		assertNotNull(stats.getTopWordCounts(5)); // this also verifies no index out of bounds
		assertNotNull(stats.getBottomWordCounts(5)); // this also verifies no index out of bounds
		assertNotNull(stats.getWordCountsForWords("test"));
	}
	
	@Test
	public void testAddWords() {
		WordStatistics stats = new WordStatistics();
		stats.addWords("One,Two Three");
		assertEquals(3, stats.getWordCounts().size());
		stats.addWords("one, four");  // words should be treated case-insensitive, and "one" was already added, so this should add only "four"
		assertEquals(4, stats.getWordCounts().size());
		assertEquals(1, stats.getWordCountsForWords("One").size());
		assertEquals(2, stats.getWordCountsForWords("One").get(0).getCount());
	}

	@Test
	public void testGetTopBottom() {
		WordStatistics stats = new WordStatistics();
		stats.addWords("One one ONE one two three three");
		assertEquals("One", stats.getTopWordCounts(1).get(0).getWord());
		assertEquals(4, stats.getTopWordCounts(1).get(0).getCount());
		assertEquals("Two", stats.getBottomWordCounts(1).get(0).getWord());
		assertEquals(1, stats.getBottomWordCounts(1).get(0).getCount());
	}
}
