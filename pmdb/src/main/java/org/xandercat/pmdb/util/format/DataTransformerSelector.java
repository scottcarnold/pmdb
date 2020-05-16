package org.xandercat.pmdb.util.format;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.thymeleaf.util.StringUtils;

/**
 * Provides the auto-detect capability for determining the best data transformer (if any) for a single movie attribute.
 * 
 * @author Scott Arnold
 */
public class DataTransformerSelector {

	private static final int MAX_SAMPLE_SIZE = 20;
	
	private String attributeName;
	private List<DataTransformer<?>> dataTransformers;
	private Map<String, Integer> parseCounts = new HashMap<String, Integer>();
	private int totalCount;
	
	public DataTransformerSelector(String attributeName, List<DataTransformer<?>> dataTransformers) {
		this.attributeName = attributeName;
		this.dataTransformers = dataTransformers;
	}
	
	public String getAttributeName() {
		return attributeName;
	}

	public void test(String value) {
		if (totalCount >= MAX_SAMPLE_SIZE || StringUtils.isEmptyOrWhitespace(value)) {
			return; // only test non-blank values and quit when sample size is reached
		}
		for (DataTransformer<?> dataTransformer : dataTransformers) {
			if (dataTransformer.isParseable(value)) {
				Integer count = parseCounts.get(dataTransformer.getName());
				if (count == null) {
					count = Integer.valueOf(0);
				}
				parseCounts.put(dataTransformer.getName(), Integer.valueOf(count.intValue()+1));
			}
		}
		totalCount++;
	}
	
	public DataTransformer<?> getDataTransformer() {
		int max = 0;
		String transformerName = null;
		for (Map.Entry<String, Integer> entry : parseCounts.entrySet()) {
			if (entry.getValue().intValue() > max) {
				transformerName = entry.getKey();
				max = entry.getValue().intValue();
			}
		}
		if ((max * 2) > totalCount) {
			// only use transformer if it could transform more than half the tested values
			for (DataTransformer<?> dataTransformer : dataTransformers) {
				if (dataTransformer.getName().equals(transformerName)) {
					return dataTransformer;
				}
			}
		}
		return null;
	}
}
