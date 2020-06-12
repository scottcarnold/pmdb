package org.xandercat.pmdb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class DoubleStatisticsTest {

	private DoubleStatistics doubleStats(Number... numbers) {
		Double[] doubles = new Double[numbers.length];
		for (int i=0; i<numbers.length; i++) {
			doubles[i] = numbers[i].doubleValue();
		}
		return doubleStats(doubles);
	}
	
	private DoubleStatistics doubleStats(Double... doubles) {
		return new DoubleStatistics(Arrays.asList(doubles));
	}
	
	@Test
	public void testMinMaxAverageMedian() {
		DoubleStatistics stats = doubleStats(8d, 6d, 4d);
		assertEquals(4, stats.getMin(), 0.001);
		assertEquals(8, stats.getMax(), 0.001);
		assertEquals(6, stats.getAverage(), 0.001);
		assertEquals(6, stats.getMedian(), 0.001);
	}
	
	@Test
	public void testMedianEven() {
		DoubleStatistics stats = doubleStats(4, 8, 6, 7);
		assertEquals(6.5, stats.getMedian(), 0.001);
	}
	
	@Test
	public void testStandardDeviation() {
		DoubleStatistics stats = doubleStats(9, 2, 5, 4, 12, 7, 8, 11, 9, 3, 7, 4, 12, 5, 4, 10, 9, 6, 9, 4);
		assertEquals(2.983, stats.getStandardDeviation(), 0.001);
	}
	
	@Test
	public void testInterquartileRangeOdd() {
		DoubleStatistics stats = doubleStats(1, 2, 5, 6, 7, 9, 12, 15, 18, 19, 27);
		assertEquals(13, stats.getInterquartileRange(), 0.001);
	}
	
	@Test
	public void testInterquartileRangeEven() {
		DoubleStatistics stats = doubleStats(3, 5, 7, 8, 9, 11, 15, 16, 20, 21);
		assertEquals(9, stats.getInterquartileRange(), 0.001);		
	}

	@Test
	public void testIQROutlier() {
		DoubleStatistics stats = doubleStats(5, 7, 10, 15, 19, 21, 21, 22, 22, 23, 23, 23, 23, 23, 24, 24, 24, 24, 25);
		assertTrue(stats.isLowOutlier(5));
		assertTrue(stats.isLowOutlier(10));
		assertFalse(stats.isLowOutlier(15));
		assertFalse(stats.isLowOutlier(100));
		assertTrue(stats.isHighOutlier(32));
		assertFalse(stats.isHighOutlier(31));
		assertTrue(stats.isOutlier(32));
		assertTrue(stats.isOutlier(10));
		assertFalse(stats.isOutlier(15));
	}
}
