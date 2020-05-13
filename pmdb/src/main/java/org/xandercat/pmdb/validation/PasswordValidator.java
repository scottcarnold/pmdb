package org.xandercat.pmdb.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.xandercat.pmdb.util.Pair;

/**
 * Validator for passwords.  Validated class can either be a String or a Pair<String>.
 * If validated class is a Pair<String>, only the Pair first value is validated.
 * 
 * @author Scott Arnold
 */
public class PasswordValidator implements ConstraintValidator<Password, Object> {

	@Override
	public void initialize(Password constraintAnnotation) {
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		String password = null;
		if (Pair.class.isAssignableFrom(value.getClass())) {
			if (((Pair<?>) value).getFirst() == null || ((Pair<?>) value).getFirst().getClass() != String.class) {
				return false;
			}
			password = (String) ((Pair<?>) value).getFirst();
		} else if (value.getClass() == String.class) {
			password = (String) value;
		}
		// implement custom password rules here
		password = password.trim();
		if (password.length() < 8 || password.length() > 72) { // 72 should be max supported by Spring BCrypt implementation; 8 was chosen arbitrarily
			return false;
		}
		return true;
	}



}
