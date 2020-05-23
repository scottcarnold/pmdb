package org.xandercat.pmdb.util.format;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.thymeleaf.util.StringUtils;

/**
 * Provides the auto-detect capability for determining the best data transformer (if any) for a single movie attribute.
 * 
 * @author Scott Arnold
 */
public class DataTransformerSelector {

	private static final int MAX_SAMPLE_SIZE = 20;
	
	private String attributeName;
	private Map<DataTransformer<?>, Integer> dataTransformerMap = new HashMap<DataTransformer<?>, Integer>();
	private int totalCount;
	
	public DataTransformerSelector(String attributeName, List<DataTransformer<?>> dataTransformers) {
		this.attributeName = attributeName;
		dataTransformers.forEach(dataTransformer -> dataTransformerMap.put(dataTransformer, Integer.valueOf(0)));
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	private void incrementParseCount(DataTransformer<?> dataTransformer) {
		Integer count = dataTransformerMap.get(dataTransformer);
		dataTransformerMap.put(dataTransformer, Integer.valueOf(count.intValue()+1));		
	}
	
	public void test(String value) {
		if (totalCount >= MAX_SAMPLE_SIZE || StringUtils.isEmptyOrWhitespace(value)) {
			return; // only test non-blank values and quit when sample size is reached
		}
		dataTransformerMap.keySet().stream()
			.filter(dataTransformer -> dataTransformer.isParseable(value))
			.forEach(this::incrementParseCount);
		totalCount++;
	}
	
	public Optional<DataTransformer<?>> getDataTransformer() {
		Optional<Map.Entry<DataTransformer<?>, Integer>> maxEntry = dataTransformerMap.entrySet().stream()
			.max(Comparator.comparing(Map.Entry::getValue));
		if ((maxEntry.get().getValue() * 2) > totalCount) {
			// only use transformer if it could transform more than half the tested values
			return Optional.of(maxEntry.get().getKey());
		}
		return Optional.empty();
	}
}
