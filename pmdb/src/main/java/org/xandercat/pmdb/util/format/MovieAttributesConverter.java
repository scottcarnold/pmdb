package org.xandercat.pmdb.util.format;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class MovieAttributesConverter implements DynamoDBTypeConverter<Map<String, String>, Map<String, String>> {

	@Override
	public Map<String, String> convert(Map<String, String> object) {
		Map<String, String> convertedMap = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : object.entrySet()) {
			convertedMap.put(FormatUtil.convertToDynamoKey(entry.getKey()), entry.getValue());
		}
		return convertedMap;
	}

	@Override
	public Map<String, String> unconvert(Map<String, String> object) {
		Map<String, String> unconvertedMap = new TreeMap<String, String>();
		for (Map.Entry<String, String> entry : object.entrySet()) {
			unconvertedMap.put(FormatUtil.convertFromDynamoKey(entry.getKey()), entry.getValue());
		}
		return unconvertedMap;
	}

}
