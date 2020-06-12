package org.xandercat.pmdb.util;

import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Statistics for double values.  This class expects the list of doubles to be non-null, contain no null values, and have a size of at least 1.
 * 
 * Standard deviation and interquartile range are lazy calculated when needed.
 * 
 * @author Scott Arnold
 */
public class DoubleStatistics extends NumberStatistics<Double> {

	public static final double IQR_MULTIPLIER = 1.5;  // standard multiplier for finding outliers using IQR
	
	private DoubleSummaryStatistics summaryStatistics;
	
	/**
	 * Construct statistics for the provided list of double values.
	 * 
	 * @param values  double values
	 */
	public DoubleStatistics(List<Double> values) {
		super(values);
		this.summaryStatistics = values.stream().collect(Collectors.summarizingDouble(value -> value));
	}

	@Override
	protected void sort(List<Double> values) {
		Collections.sort(values);
	}

	@Override
	protected Double parse(String value) {
		try {
			return Double.valueOf(value);
		} catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public Double getMin() {
		return summaryStatistics.getMin();
	}

	@Override
	public Double getMax() {
		return summaryStatistics.getMax();
	}

	@Override
	public double getAverage() {
		return summaryStatistics.getAverage();
	}
}
