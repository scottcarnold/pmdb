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
public class DoubleStatistics {

	public static final double IQR_MULTIPLIER = 1.5;  // standard multiplier for finding outliers using IQR
	
	private List<Double> values;
	private DoubleSummaryStatistics summaryStatistics;
	private Double standardDeviation;
	private Double interquartileRange;
	private Double iqrQ1;
	private Double iqrQ3;
	
	/**
	 * Construct statistics for the provided list of double values.
	 * 
	 * @param values  double values
	 */
	public DoubleStatistics(List<Double> values) {
		if (values == null || values.size() < 1) {
			throw new IllegalArgumentException("List of values must be non null and contain at least 1 value.");
		}
		this.values = values;
		Collections.sort(values);
		this.summaryStatistics = values.stream().collect(Collectors.summarizingDouble(value -> value));
	}

	/**
	 * Returns minimum value from list of doubles.
	 * 
	 * @return minimum value
	 */
	public double getMin() {
		return summaryStatistics.getMin();
	}
	
	/**
	 * Returns maximum value from list of doubles.
	 * 
	 * @return maximum value
	 */
	public double getMax() {
		return summaryStatistics.getMax();
	}
	
	/**
	 * Returns average value from list of doubles.
	 * 
	 * @return average value
	 */
	public double getAverage() {
		return summaryStatistics.getAverage();
	}
	
	/**
	 * Returns median value from list of doubles.
	 * 
	 * @return median value
	 */
	public double getMedian() {
		return calculateMedian(values);
	}
	
	/**
	 * Returns standard deviation from list of doubles.
	 * 
	 * @return standard deviation
	 */
	public double getStandardDeviation() {
		if (standardDeviation == null) {
			calculateStandardDeviation();
		}
		return standardDeviation;
	}
	
	/**
	 * Returns interquartile range from list of doubles.
	 * 
	 * @return interquartile range
	 */
	public double getInterquartileRange() {
		if (interquartileRange == null) {
			calculateInterquartileRange();
		}
		return interquartileRange;
	}
	
	private Double parseDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Returns whether or not the provided value is an outlier from the list of doubles using the 1.5xIQR rule.
	 * If the value cannot be parsed, it is considered an outlier.
	 * 
	 * @param value double value to test
	 * 
	 * @return whether or not value is an outlier as compared to the internal list of doubles.
	 */
	public boolean isOutlier(String value) {
		Double doubleValue = parseDouble(value);
		return (doubleValue == null || isOutlier(doubleValue));
	}
	
	/**
	 * Returns whether or not the provided value is an outlier from the list of doubles using the 1.5xIQR rule.
	 * 
	 * @param value double value to test
	 * 
	 * @return whether or not value is an outlier as compared to the internal list of doubles.
	 */
	public boolean isOutlier(double value) {
		return isHighOutlier(value) || isLowOutlier(value);
	}

	/**
	 * Returns whether or not the provided value is an outlier on the high end from the list of doubles using the 1.5xIQR rule.
	 * If the value cannot be parsed, it is considered an outlier.
	 * 
	 * @param value double value to test
	 * 
	 * @return whether or not value is an outlier on the high end as compared to the internal list of doubles.
	 */
	public boolean isHighOutlier(String value) {
		Double doubleValue = parseDouble(value);
		return (doubleValue == null || isHighOutlier(doubleValue));
	}

	/**
	 * Returns whether or not the provided value is an outlier on the high end from the list of doubles using the 1.5xIQR rule.
	 * 
	 * @param value double value to test
	 * 
	 * @return whether or not value is an outlier on the high end as compared to the internal list of doubles.
	 */
	public boolean isHighOutlier(double value) {
		double iqr = getInterquartileRange();
		return (value > (iqrQ3 + IQR_MULTIPLIER * iqr));
	}
	
	/**
	 * Returns whether or not the provided value is an outlier on the low end from the list of doubles using the 1.5xIQR rule.
	 * If the value cannot be parsed, it is considered an outlier.
	 * 
	 * @param value double value to test
	 * 
	 * @return whether or not value is an outlier on the low end as compared to the internal list of doubles.
	 */
	public boolean isLowOutlier(String value) {
		Double doubleValue = parseDouble(value);
		return (doubleValue == null || isLowOutlier(doubleValue));
	}
	
	/**
	 * Returns whether or not the provided value is an outlier on the low end from the list of doubles using the 1.5xIQR rule.
	 * 
	 * @param value double value to test
	 * 
	 * @return whether or not value is an outlier on the low end as compared to the internal list of doubles.
	 */
	public boolean isLowOutlier(double value) {
		double iqr = getInterquartileRange();
		return (value < (iqrQ1 - IQR_MULTIPLIER * iqr));
	}
	
	private void calculateStandardDeviation() {
		double intermediateSum = 0;
		for (Double value : values) {
			intermediateSum += Math.pow(value - summaryStatistics.getAverage(), 2);
		}
		this.standardDeviation = Math.sqrt(intermediateSum / summaryStatistics.getCount());
	}
	
	private void calculateInterquartileRange() {
		int midpoint = values.size() / 2;
		List<Double> lowerHalf = values.subList(0, midpoint);
		List<Double> upperHalf = values.subList(midpoint+(values.size() % 2), values.size());
		this.iqrQ1 = calculateMedian(lowerHalf);
		this.iqrQ3 = calculateMedian(upperHalf);
		this.interquartileRange = iqrQ3 - iqrQ1;
	}
	
	private double calculateMedian(List<Double> values) {
		int midpointIndex = values.size() / 2;
		if (values.size() % 2 == 0) {
			return (values.get(midpointIndex -1) + values.get(midpointIndex)) / 2d;
		} else {
			return values.get(midpointIndex);
		}
	}
}
