package org.xandercat.pmdb.util;

import java.util.List;

/**
 * Statistics for numbers.  This class expects the list of numbers to be non-null, contain no null values, and have a size of at least 1.
 * 
 * Standard deviation and interquartile range are lazy calculated when needed.
 * 
 * @author Scott Arnold
 */
public abstract class NumberStatistics<T extends Number> {

	public static final double IQR_MULTIPLIER = 1.5;  // standard multiplier for finding outliers using IQR
	
	private List<T> values;
	private Double standardDeviation;
	private Double interquartileRange;
	private Double iqrQ1;
	private Double iqrQ3;
	
	/**
	 * Construct statistics for the provided list of values.
	 * 
	 * @param values  double values
	 */
	public NumberStatistics(List<T> values) {
		if (values == null || values.size() < 1) {
			throw new IllegalArgumentException("List of values must be non null and contain at least 1 value.");
		}
		this.values = values;
		sort(values);
	}

	/**
	 * Sorts the list of values in natural order.
	 * 
	 * @param values values to sort
	 */
	protected abstract void sort(List<T> values);
	
	/**
	 * Return number parsed from given string.  If value cannot be parsed, null should be returned.
	 * 
	 * @param value value to parse
	 * 
	 * @return parsed value
	 */
	protected abstract T parse(String value);
	
	/**
	 * Returns the minimum number from the list of numbers.
	 * 
	 * @return minimum
	 */
	public abstract T getMin();
	
	/**
	 * Returns the maximum number from the list of numbers.
	 * 
	 * @return maximum
	 */
	public abstract T getMax();
	
	/**
	 * Returns the average from the list of numbers.
	 * 
	 * @return average
	 */
	public abstract double getAverage();
	
	/**
	 * Returns median value from list of numbers.  Returned number is a double due to
	 * rule of taking average of two middle values for a list with an even quantity of numbers.
	 * 
	 * @return median value
	 */
	public double getMedian() {
		return calculateMedian(values);
	}
	
	/**
	 * Returns standard deviation from list of numbers.
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
	 * Returns interquartile range from list of numbers.
	 * 
	 * @return interquartile range
	 */
	public double getInterquartileRange() {
		if (interquartileRange == null) {
			calculateInterquartileRange();
		}
		return interquartileRange;
	}
	
	/**
	 * Returns whether or not the provided value is an outlier from the list of doubles using the 1.5xIQR rule.
	 * If the value cannot be parsed, it is considered an outlier.
	 * 
	 * @param value value to test
	 * 
	 * @return whether or not value is an outlier as compared to the internal list of numbers.
	 */
	public boolean isOutlier(String value) {
		T parsedValue = parse(value);
		return (parsedValue == null || isOutlier(parsedValue));
	}
	
	/**
	 * Returns whether or not the provided value is an outlier from the list of numbers using the 1.5xIQR rule.
	 * 
	 * @param value value to test
	 * 
	 * @return whether or not value is an outlier as compared to the internal list of numbers.
	 */
	public boolean isOutlier(T value) {
		return isHighOutlier(value) || isLowOutlier(value);
	}

	/**
	 * Returns whether or not the provided value is an outlier on the high end from the list of numbers using the 1.5xIQR rule.
	 * If the value cannot be parsed, it is considered an outlier.
	 * 
	 * @param value value to test
	 * 
	 * @return whether or not value is an outlier on the high end as compared to the internal list of numbers.
	 */
	public boolean isHighOutlier(String value) {
		T parsedValue = parse(value);
		return (parsedValue == null || isHighOutlier(parsedValue));
	}

	/**
	 * Returns whether or not the provided value is an outlier on the high end from the list of numbers using the 1.5xIQR rule.
	 * If the value is null, it is considered an outlier.
	 * 
	 * @param value value to test
	 * 
	 * @return whether or not value is an outlier on the high end as compared to the internal list of numbers.
	 */
	public boolean isHighOutlier(T value) {
		if (value == null) {
			return true;
		}
		double iqr = getInterquartileRange();
		return (value.doubleValue() > (iqrQ3 + IQR_MULTIPLIER * iqr));
	}
	
	/**
	 * Returns whether or not the provided value is an outlier on the low end from the list of numbers using the 1.5xIQR rule.
	 * If the value cannot be parsed, it is considered an outlier.
	 * 
	 * @param value value to test
	 * 
	 * @return whether or not value is an outlier on the low end as compared to the internal list of numbers.
	 */
	public boolean isLowOutlier(String value) {
		T parsedValue = parse(value);
		return (parsedValue == null || isLowOutlier(parsedValue));
	}
	
	/**
	 * Returns whether or not the provided value is an outlier on the low end from the list of numbers using the 1.5xIQR rule.
	 * If the value is null, it is considered an outlier.
	 * 
	 * @param value value to test
	 * 
	 * @return whether or not value is an outlier on the low end as compared to the internal list of numbers.
	 */
	public boolean isLowOutlier(T value) {
		if (value == null) {
			return true;
		}
		double iqr = getInterquartileRange();
		return (value.doubleValue() < (iqrQ1 - IQR_MULTIPLIER * iqr));
	}
	
	private void calculateStandardDeviation() {
		double intermediateSum = 0;
		double average = getAverage();
		for (Number value : values) {
			intermediateSum += Math.pow(value.doubleValue() - average, 2);
		}
		this.standardDeviation = Math.sqrt(intermediateSum / (double) values.size());
	}
	
	private void calculateInterquartileRange() {
		if (values.size() == 1) {
			this.iqrQ1 = values.get(0).doubleValue();
			this.iqrQ3 = values.get(0).doubleValue();
		} else {
			int midpoint = values.size() / 2;
			List<T> lowerHalf = values.subList(0, midpoint);
			List<T> upperHalf = values.subList(midpoint+(values.size() % 2), values.size());
			this.iqrQ1 = calculateMedian(lowerHalf);
			this.iqrQ3 = calculateMedian(upperHalf);
		}
		this.interquartileRange = iqrQ3 - iqrQ1;
	}
	
	private double calculateMedian(List<T> values) {
		int midpointIndex = values.size() / 2;
		if (values.size() % 2 == 0) {
			return (values.get(midpointIndex -1).doubleValue() + values.get(midpointIndex).doubleValue()) / 2d;
		} else {
			return values.get(midpointIndex).doubleValue();
		}
	}

}
