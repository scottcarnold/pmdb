package org.xandercat.pmdb.validation;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xandercat.pmdb.util.Pair;

public class ValuesMatchValidatorTest {

	private static ValuesMatchValidator validator;
	
	@Mock
	private ConstraintValidatorContext context;
	
	@BeforeAll
	public static void beforeAll() {
		validator = new ValuesMatchValidator();
	}
	
	@Test
	public void testEmptyValues() {
		Pair<String> pair = new Pair<>();
		assertTrue(validator.isValid(pair, context));
		pair.setFirst("");
		pair.setSecond("");
		assertTrue(validator.isValid(pair, context));
	}
	
	@Test
	public void testMatching() {
		Pair<String> pair = new Pair<>();
		pair.setFirst("Hello");
		pair.setSecond("Hello");
		assertTrue(validator.isValid(pair, context));
		pair.setSecond("hello");
		assertFalse(validator.isValid(pair, context));
	}
}
