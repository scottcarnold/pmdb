package org.xandercat.pmdb.util.format;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Data formatter selector to be used to select the best data formatter for a group of tested values.
 * 
 * @author Scott Arnold
 */
public class DataFormatterSelector {

	private class FormatterCount {
		private DataFormatter dataFormatter;
		private int count;
		public FormatterCount(DataFormatter dataFormatter) {
			this.dataFormatter = dataFormatter;
		}
		public int getCount() {
			return count;
		}
	}
	
	private int maxSampleSize = 20;
	private int sampleSize;
	private String attributeName;
	private List<FormatterCount> formatterCounts = new ArrayList<FormatterCount>();
	
	public DataFormatterSelector(String attributeName, List<DataFormatter> dataFormatters) {
		this.attributeName = attributeName;
		dataFormatters.forEach(dataFormatter -> formatterCounts.add(new FormatterCount(dataFormatter)));
	}
	
	/**
	 * Sets how many values should be tested in order to make selection on best formatter to use.
	 * 
	 * @param sampleSize sample size
	 */
	public void setSampleSize(int sampleSize) {
		this.sampleSize = sampleSize;
	}
	
	/**
	 * Returns the name of the attribute this selector is for.
	 * 
	 * @return name of the attribute
	 */
	public String getAttributeName() {
		return attributeName;
	}
	
	/**
	 * Tests a value against the collection of data formatters.  Value should be a value for the attribute this selector represents.
	 * 
	 * @param value value to be tested
	 */
	public void test(String value) {
		if (sampleSize >= maxSampleSize || FormatUtil.isBlank(value)) {
			return; // only test non-blank values, and stop testing after reaching max sample size
		}
		formatterCounts.stream()
				.filter(formatterCount -> formatterCount.dataFormatter.isAcceptable(value))
				.forEach(formatterCount -> formatterCount.count++);
		sampleSize++;
	}
	
	/**
	 * Returns the data formatter selected for the attribute.  For a data formatter to be selected, it must
	 * be able to accept more than half the tested values.  If two or more data formatters are tied for best,
	 * the data formatter selected from that group is indeterminate.
	 * 
	 * @return data formatter (empty if no data formatter is appropriate)
	 */
	public Optional<DataFormatter> getDataFormatter() {
		Optional<FormatterCount> maxFormatterCount = formatterCounts.stream()
				.max(Comparator.comparing(FormatterCount::getCount));
		if (maxFormatterCount.isPresent() && maxFormatterCount.get().count * 2 > sampleSize) {
			// only use data formatter if it could accept more than half the tested values
			return Optional.of(maxFormatterCount.get().dataFormatter);
		}
		return Optional.empty();
	}
}
