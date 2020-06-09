package org.xandercat.pmdb.util.format;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MovieAttributesConverterTest {

	private static MovieAttributesConverter converter;
	
	@BeforeAll
	public static void beforeAll() {
		converter = new MovieAttributesConverter();
	}
	
	@Test
	public void testEmptyMap() {
		Map<String, String> map = new HashMap<>();
		Map<String, String> cMap = converter.convert(map);
		assertNotNull(cMap);
		assertEquals(0, cMap.size());
	}
	
	@Test
	public void testConvertToDynamo() {
		Map<String, String> map = new HashMap<>();
		map.put("Attribute With Spaces", "anything");
		Map<String, String> cMap = converter.convert(map);
		assertEquals(1, cMap.size());
		assertEquals("Attribute_With_Spaces", cMap.keySet().stream().findAny().get());
		assertEquals("anything", cMap.get("Attribute_With_Spaces"));
	}

	@Test
	public void testConvertFromDynamo() {
		Map<String, String> map = new HashMap<>();
		map.put("Attribute_With_Spaces", "anything");
		Map<String, String> cMap = converter.unconvert(map);
		assertEquals(1, cMap.size());
		assertEquals("Attribute With Spaces", cMap.keySet().stream().findAny().get());
		assertEquals("anything", cMap.get("Attribute With Spaces"));		
	}
}
