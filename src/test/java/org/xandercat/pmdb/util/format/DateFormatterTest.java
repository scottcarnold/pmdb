package org.xandercat.pmdb.util.format;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

public class DateFormatterTest {

	@Test
	public void testBasicDateFormatting() {
		DateFormatter formatter = new DateFormatter().parseFormat("MM/dd/yyyy").displayFormat("MM-dd-yyyy");
		assertEquals("03-04-2010", formatter.displayValue("03/04/2010"));
		assertEquals("20100304", formatter.sortValue("03/04/2010"));
		assertTrue(formatter.isAcceptable("03/04/2010"));
		assertFalse(formatter.isAcceptable("randomtext"));
	}
	
	@Test
	public void testDateFormattingBlankValues() {
		AbstractDataFormatter<Date> formatter = new DateFormatter()
				.parseFormat("MM/dd/yyyy").displayFormat("MM-dd-yyyy")
				.defaultDisplayValue("Default").defaultSortValue("DefaultSort");
		assertTrue(formatter.isAcceptable(null));
		assertTrue(formatter.isAcceptable(""));
		assertEquals("Default", formatter.displayValue(null));
		assertEquals("DefaultSort", formatter.sortValue(null));
	}

}
