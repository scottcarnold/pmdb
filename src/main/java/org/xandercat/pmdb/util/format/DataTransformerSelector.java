package org.xandercat.pmdb.util.format;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides the auto-detect capability for determining the best data transformer (if any) for a single movie attribute.
 * If two or more transformers are tied for best, transformer will be selected based on it's order in the original list.
 * 
 * @author Scott Arnold
 */
public class DataTransformerSelector {

	private static final Logger LOGGER = LogManager.getLogger(DataTransformerSelector.class);
	
	private class Priority implements Comparable<Priority> {
		private int priority;
		private int parseCount;
		public Priority(int priority) {
			this.priority = priority;
		}
		@Override
		public int compareTo(Priority o) {
			if (parseCount == o.parseCount) {
				return o.priority - priority; // higher values should sort to top
			} else {
				return parseCount - o.parseCount;
			}
		}
	}
	
	private static final int MAX_SAMPLE_SIZE = 20;
	
	private String attributeName;
	private Map<DataTransformer<?>, Priority> dataTransformerMap = new HashMap<DataTransformer<?>, Priority>();
	private int totalCount;
	
	public DataTransformerSelector(String attributeName, List<DataTransformer<?>> dataTransformers) {
		this.attributeName = attributeName;
		for (int i=0; i<dataTransformers.size(); i++) {
			dataTransformerMap.put(dataTransformers.get(i), new Priority(i));
		}
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	private void incrementParseCount(DataTransformer<?> dataTransformer) {
		Priority priority = dataTransformerMap.get(dataTransformer);
		priority.parseCount++;	
	}
	
	public void test(String value) {
		if (totalCount >= MAX_SAMPLE_SIZE || FormatUtil.isBlank(value)) {
			return; // only test non-blank values and quit when sample size is reached
		}
		dataTransformerMap.keySet().stream()
			.filter(dataTransformer -> dataTransformer.isParseable(value))
			.forEach(this::incrementParseCount);
		totalCount++;
	}
	
	public Optional<DataTransformer<?>> getDataTransformer() {
		Optional<Map.Entry<DataTransformer<?>, Priority>> maxEntry = dataTransformerMap.entrySet().stream()
			.max(Comparator.comparing(Map.Entry::getValue));
		if ((maxEntry.get().getValue().parseCount * 2) > totalCount) {
			// only use transformer if it could transform more than half the tested values
			return Optional.of(maxEntry.get().getKey());
		}
		return Optional.empty();
	}
}
