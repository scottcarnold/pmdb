package org.xandercat.pmdb.util.format;

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
		assertEquals("abc", FormatUtil.formatAlphaNumeric("abc"));
	}
	
	@Test
	public void testConvertDynamoKey() {
		assertEquals("Imdb_Id", FormatUtil.convertToDynamoKey("Imdb Id"));
		assertEquals("Imdb Id", FormatUtil.convertFromDynamoKey("Imdb_Id"));
	}
	
	@Test
	public void testValidUsername() {
		assertTrue(FormatUtil.isValidUsername("AaBbCc9"));
		assertTrue(FormatUtil.isValidUsername("joefish@pacificocean.com"));
		assertTrue(FormatUtil.isValidUsername("joe_fish-roberts@pacificocean.com"));
		assertFalse(FormatUtil.isValidUsername(null));
		assertFalse(FormatUtil.isValidUsername(""));
		assertFalse(FormatUtil.isValidUsername(" "));
		assertFalse(FormatUtil.isValidUsername("Jeff "));
		assertFalse(FormatUtil.isValidUsername("Jeff Henry"));
	}
	
	@Test
	public void testFormatAsNumber() {
		assertEquals("123", FormatUtil.formatAsNumber(" 123"));
		assertEquals("12.50", FormatUtil.formatAsNumber("$12.50"));
		assertEquals("19500", FormatUtil.formatAsNumber("19,500"));
		assertEquals("19500.75", FormatUtil.formatAsNumber("$19,500.75"));
		assertEquals("19500.75", FormatUtil.formatAsNumber("19.500,75"));
		assertEquals("00", FormatUtil.formatAsNumber("00"));
	}
}
