package org.xandercat.pmdb.form.movie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MovieFormTest {

	@Test
	public void testReflectionGetAttribute() {
		MovieForm form = new MovieForm();
		form.setAttrKey12("key12");
		form.setAttrValue12("value12");
		String key12 = form.getAttrKey(12);
		String value12 = form.getAttrValue(12);
		assertEquals("key12", key12);
		assertEquals("value12", value12);
	}

	@Test
	public void testReflectionSetAttribute() {
		MovieForm form = new MovieForm();
		form.setAttrKey(12, "key12");
		form.setAttrValue(12, "value12");
		String key12 = form.getAttrKey12();
		String value12 = form.getAttrValue12();
		assertEquals("key12", key12);
		assertEquals("value12", value12);
	}
}
