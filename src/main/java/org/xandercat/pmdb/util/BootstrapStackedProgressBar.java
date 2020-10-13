package org.xandercat.pmdb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class for aggregating data for a Bootstrap stacked progress bar with minimum bar size and 
 * optional global maximum value.  This is intended for use not as a progress bar, but as a linear chart
 * of multiple data points.
 * 
 * Minimum bar size should be in range 0 - 1 inclusive (use 0 for no minimum; e.g. 0.1 = 10%).
 * 
 * Global maximum, if desired, is an external maximum that values within the bars may not reach.  If not specified, 
 * the maximum defaults to the maximum value from the list of provided bars.
 * 
 * The finalize() method should be called prior to using the bars in the view.
 * 
 * @author Scott Arnold
 */
public class BootstrapStackedProgressBar {

	/**
	 * Class to represent a component bar within the stacked bar.
	 * 
	 * @author Scott Arnold
	 */
	public static class Bar implements Comparable<Bar> {
		public Bar(double value, String label, String cssClass) {
			this.value = value;
			this.label = label;
			this.cssClass = cssClass;
		}
		private double value;        // the original value to general the bar off of; can be anything
		private double percent;      // the calculated percent in range 0 - 1 that the bar should consume in the view
		private String cssClass;     // css class to assign to the bar
		private String label;        // label for bar, if not using value, or to be used in combination with value
		public double getValue() {
			return value;
		}
		public String getLabel() {
			return label;
		}
		public double getPercent() {
			return percent;
		}
		public String getCssClass() {
			return cssClass;
		}
		@Override
		public int compareTo(Bar o) {
			return (value < o.value)? -1 : 1;
		}
	}
	
	private double minimumBarSize;
	private Double globalMaximum;
	private List<Bar> bars = new ArrayList<Bar>();
	private boolean finalized;
	
	/**
	 * Construct a new stacked bar with given minimum component bar size in percent 0 - 1.  When using
	 * this constructor, the stacked bar will always fill the entire range of the overall bar with a maximum
	 * overall bar value set to the max value from the list of component bars.
	 * 
	 * @param minimumBarSize  minimum bar size for each component bar as a percentage in range 0 - 1
	 */
	public BootstrapStackedProgressBar(double minimumBarSize) {
		this(minimumBarSize, null);
	}
	
	/**
	 * Construct a new stacked bar with given minimum component bar size and explicit maximum raw value
	 * for the overall bar.  
	 * 
	 * @param minimumBarSize        minimum bar size for each component bar as a percentage in range 0 - 1
	 * @param globalMaximumValue    maximum value a bar can have
	 */
	public BootstrapStackedProgressBar(double minimumBarSize, Double globalMaximumValue) {
		if (minimumBarSize < 0 || minimumBarSize > 1) {
			throw new IllegalArgumentException("minimum bar size must be between 0 and 1 inclusive (use 0 for no minimum)");
		}
		this.minimumBarSize = minimumBarSize;
		this.globalMaximum = globalMaximumValue;
	}

	/**
	 * Add a new component bar to the stacked bar.  All component bars should be added prior to calling finalized().
	 * 
	 * @param value     raw value the bar is meant to represent
	 * @param label     label for the bar
	 * @param cssClass  CSS class for the bar
	 */
	public void addBar(double value, String label, String cssClass) {
		if (finalized) {
			throw new IllegalArgumentException("Bars cannot be added after finalized has already been called");
		}
		if (globalMaximum != null && value > globalMaximum.doubleValue()) {
			throw new IllegalArgumentException("Bar value exceeds set global maximum value.");
		}
		this.bars.add(new Bar(value, label, cssClass));
		if ((bars.size() * minimumBarSize) > 1) {
			throw new IllegalArgumentException("Number of bars paired with set minimum bar size exceeds 100%");
		}
	}
	
	/**
	 * Returns the list of component bars for the stacked bar.
	 * 
	 * @return component bars
	 */
	public List<Bar> getBars() {
		return Collections.unmodifiableList(bars);
	}
	
	/**
	 * Computes and sets the bar percentages for all component bars.  This method should always be called, and only 
	 * called after all component bars have been added.
	 *  
	 * @return self
	 */
	public BootstrapStackedProgressBar finalized() {
		this.finalized = true;
		Collections.sort(bars);
		if (globalMaximum == null && bars.size() > 0) {
			globalMaximum = bars.stream().mapToDouble(Bar::getValue).max().getAsDouble();
		}
		double priorPir = 0;
		for (Bar bar : bars) {
			double pir = NumberStatistics.getPercentInRange(0, globalMaximum, bar.value);
			bar.percent = minimumBarSize + (1 - (minimumBarSize * bars.size())) * (pir - priorPir);
			priorPir = pir;
		}
		return this;
	}
	
	/**
	 * Returns a bar for the provided value that fits the model of this stacked bar.  Can be used to portray
	 * how a single data point compares to the overall data represented by the stacked bar.  Only call this method
	 * after having called finalized().
	 * 
	 * @param value     value to build bar for
	 * @param label     label for bar
	 * @param cssClass  CSS class for bar
	 * 
	 * @return bar for given value that can be directly compared to the stacked bar
	 */
	public Bar getComparisonBar(double value, String label, String cssClass) {
		if (!finalized) {
			throw new UnsupportedOperationException("finalized() must be called prior to calling this method.");
		}
		Bar comparisonBar = new Bar(value, label, cssClass);
		double barPercent = 0;
		Bar priorBar = null;
		for (Bar bar : bars) {
			double priorValue = 0;
			if (priorBar != null) {
				priorValue = priorBar.value;
			}
			if (value < bar.value) {
				barPercent += bar.percent * NumberStatistics.getPercentInRange(priorValue, bar.value, value);
				comparisonBar.percent = barPercent;
				return comparisonBar;
			} else {
				barPercent += bar.percent;
			}
			priorBar = bar;
		}
		comparisonBar.percent = 1;
		return comparisonBar;
	}
}
