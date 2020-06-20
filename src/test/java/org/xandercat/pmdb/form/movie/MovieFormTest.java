package org.xandercat.pmdb.form.movie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.xandercat.pmdb.dto.Movie;

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
	
	@Test
	public void testMovieConstructorAndToMovie() {
		Movie movie = new Movie();
		movie.setId("id");
		movie.setCollectionId("cId");
		movie.setTitle("title");
		movie.addAttribute("One", "vone");
		MovieForm form = new MovieForm(movie);
		assertEquals("title", form.getTitle());
		assertEquals("id", form.getId());
		assertEquals("cId", form.getCollectionId());
		assertFalse(form.isAttrPairEmpty(0));
		assertEquals("One", form.getAttrKey(0));
		assertEquals("One", form.getAttrKey0());
		assertEquals("vone", form.getAttrValue(0));
		assertEquals("vone", form.getAttrValue0());
		assertTrue(form.isAttrPairEmpty(1));
		movie = form.toMovie();
		assertEquals("title", movie.getTitle());
		assertEquals("id", movie.getId());
		assertEquals("cId", movie.getCollectionId());
		assertEquals(1, movie.getAttributes().size());
	}
}
