package org.xandercat.pmdb.validation;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class UsernameValidatorTest {

	private static UsernameValidator validator;
	
	@Mock
	private ConstraintValidatorContext context;
	
	@BeforeAll
	public static void beforeAll() {
		validator = new UsernameValidator();
	}

	@Test
	public void testUsernameValidation() {
		assertTrue(validator.isValid("jeff", context));
		assertTrue(validator.isValid("Jeff_Jeff@no-where.com", context));
		assertTrue(validator.isValid("Jeff15", context));
		assertFalse(validator.isValid(null, context));
		assertFalse(validator.isValid("", context));
		assertFalse(validator.isValid(" ", context));
		assertFalse(validator.isValid("Jeff Jeff", context));
		assertFalse(validator.isValid("Jeff!@#$%^", context));
	}
}
