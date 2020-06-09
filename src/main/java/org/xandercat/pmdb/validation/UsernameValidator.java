package org.xandercat.pmdb.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.xandercat.pmdb.util.format.FormatUtil;

/**
 * Validator class for usernames.  Usernames must conform to valid username format dictated by the FormatUtil class.
 * 
 * @author Scott Arnold
 */
public class UsernameValidator implements ConstraintValidator<Username, String> {

	@Override
	public void initialize(Username constraintAnnotation) {
	}

	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		return FormatUtil.isValidUsername(username);
	}



}
