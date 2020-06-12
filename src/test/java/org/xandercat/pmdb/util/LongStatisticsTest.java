package org.xandercat.pmdb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class LongStatisticsTest {

	private LongStatistics longStats(Number... numbers) {
		Long[] longs = new Long[numbers.length];
		for (int i=0; i<numbers.length; i++) {
			longs[i] = numbers[i].longValue();
		}
		return longStats(longs);
	}
	
	private LongStatistics longStats(Long... longs) {
		return new LongStatistics(Arrays.asList(longs));
	}
	
	@Test
	public void testMinMaxAverageMedian() {
		LongStatistics stats = longStats(8, 6, 4);
		assertEquals(4, stats.getMin());
		assertEquals(8, stats.getMax());
		assertEquals(6, stats.getAverage(), 0.001);
		assertEquals(6, stats.getMedian(), 0.001);
	}
	
	@Test
	public void testMedianEven() {
		LongStatistics stats = longStats(4, 8, 6, 7);
		assertEquals(6.5, stats.getMedian(), 0.001);
	}
	
	@Test
	public void testStandardDeviation() {
		LongStatistics stats = longStats(9, 2, 5, 4, 12, 7, 8, 11, 9, 3, 7, 4, 12, 5, 4, 10, 9, 6, 9, 4);
		assertEquals(2.983, stats.getStandardDeviation(), 0.001);
	}
	
	@Test
	public void testInterquartileRangeOdd() {
		LongStatistics stats = longStats(1, 2, 5, 6, 7, 9, 12, 15, 18, 19, 27);
		assertEquals(13, stats.getInterquartileRange(), 0.001);
	}
	
	@Test
	public void testInterquartileRangeEven() {
		LongStatistics stats = longStats(3, 5, 7, 8, 9, 11, 15, 16, 20, 21);
		assertEquals(9, stats.getInterquartileRange(), 0.001);		
	}

	@Test
	public void testIQROutlier() {
		LongStatistics stats = longStats(5, 7, 10, 15, 19, 21, 21, 22, 22, 23, 23, 23, 23, 23, 24, 24, 24, 24, 25);
		assertTrue(stats.isLowOutlier(5l));
		assertTrue(stats.isLowOutlier(10l));
		assertFalse(stats.isLowOutlier(15l));
		assertFalse(stats.isLowOutlier(100l));
		assertTrue(stats.isHighOutlier(32l));
		assertFalse(stats.isHighOutlier(31l));
		assertTrue(stats.isOutlier(32l));
		assertTrue(stats.isOutlier(10l));
		assertFalse(stats.isOutlier(15l));
	}
	
	@Test
	public void testSmallList() {
		LongStatistics stats = longStats(5, 5);
		assertEquals(5, stats.getAverage(), 0.001);
		assertEquals(5, stats.getMedian(), 0.001);
		assertEquals(5, stats.getMin());
		assertEquals(5, stats.getMax());
		assertEquals(0, stats.getInterquartileRange());
	}
	
	@Test
	public void testMinimalList() {
		LongStatistics stats = longStats(5);
		assertEquals(5, stats.getAverage(), 0.001);
		assertEquals(5, stats.getMedian(), 0.001);
		assertEquals(5, stats.getMin());
		assertEquals(5, stats.getMax());
		assertEquals(0, stats.getInterquartileRange());		
	}

}
