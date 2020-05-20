package org.xandercat.pmdb.util;

import org.junit.jupiter.api.Test;
import org.xandercat.pmdb.util.format.FormatUtil;

import static org.junit.jupiter.api.Assertions.*;

public class FormatUtilTest {

	@Test
	public void testTitleCase() {
		String testString = "  hi thERE  ";
		String tcString = FormatUtil.titleCase(testString);
		assertEquals("  Hi There  ", tcString);
		assertNull(FormatUtil.titleCase(null));
	}
	
	@Test
	public void testAlphaNumeric() {
		String testCase = "%^Frog 24!";
		assertFalse(FormatUtil.isAlphaNumeric(testCase, true));
		assertTrue(FormatUtil.isAlphaNumeric(null, true));
		assertEquals("Frog 24", FormatUtil.formatAlphaNumeric(testCase));
	}
	
	@Test
	public void testConvertDynamoKey() {
		assertEquals("Imdb_Id", FormatUtil.convertToDynamoKey("Imdb Id"));
		assertEquals("Imdb Id", FormatUtil.convertFromDynamoKey("Imdb_Id"));
	}
}
