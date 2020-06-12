package org.xandercat.pmdb.util;

import java.util.Collections;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;

/**
 * Statistics for long values.  This class expects the list of longs to be non-null, contain no null values, and have a size of at least 1.
 * 
 * Standard deviation and interquartile range are lazy calculated when needed.
 * 
 * @author Scott Arnold
 */
public class LongStatistics extends NumberStatistics<Long> {

	private LongSummaryStatistics summaryStatistics;

	/**
	 * Construct statistics for the provided list of long values.
	 * 
	 * @param values  double values
	 */
	public LongStatistics(List<Long> values) {
		super(values);
		this.summaryStatistics = values.stream().collect(Collectors.summarizingLong(value -> value));
	}

	@Override
	protected void sort(List<Long> values) {
		Collections.sort(values);
	}
	
	@Override
	protected Long parse(String value) {
		try {
			return Long.valueOf(value);
		} catch (Exception e) { }
		return null;
	}
	
	@Override
	public Long getMin() {
		return summaryStatistics.getMin();
	}

	@Override
	public Long getMax() {
		return summaryStatistics.getMax();
	}

	@Override
	public double getAverage() {
		return summaryStatistics.getAverage();
	}

}
