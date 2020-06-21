package org.xandercat.pmdb.util.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class DataFormatterSelectorTest {

	@Test
	public void testFormatterSelection() {
		List<DataFormatter> formatters = new ArrayList<DataFormatter>();
		LongFormatter longFormatter = new LongFormatter();
		DateFormatter dateFormatter = new DateFormatter().parseFormat("MM/dd/yyyy");
		formatters.add(longFormatter);
		formatters.add(dateFormatter);
		DataFormatterSelector selector = new DataFormatterSelector("Long", formatters);
		assertEquals("Long", selector.getAttributeName());
		selector.test("1,975");
		selector.test("2,540");
		selector.test("nonsense");
		selector.test("555");
		Optional<DataFormatter> formatter = selector.getDataFormatter();
		assertTrue(formatter.isPresent());
		assertEquals(longFormatter, formatter.get());
	}

}
