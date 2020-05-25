package org.xandercat.pmdb.validation;

import static org.junit.jupiter.api.Assertions.*;

import javax.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.xandercat.pmdb.util.Pair;

public class PasswordValidatorTest {

	private static PasswordValidator validator;
	
	@Mock
	private ConstraintValidatorContext cvContext;
	
	@BeforeAll
	public static void beforeAll() {
		validator = new PasswordValidator();
	}
	
	@Test
	public void testEmptyPasswordOkay() {
		// empty passwords are allowed by design to support passwords fields in forms where the password is not changed
		assertTrue(validator.isValid(null, cvContext));
		assertTrue(validator.isValid("", cvContext));
		Pair<String> pair = new Pair<>();
		assertTrue(validator.isValid(pair, cvContext));
		pair.setFirst("");
		assertTrue(validator.isValid(pair, cvContext));
	}
	
	@Test
	public void testInvalidPassword() {
		assertFalse(validator.isValid("a", cvContext));
		assertFalse(validator.isValid("1234567890123456789012345678901234567890123456789012345678901234567890123", cvContext));
	}

}
