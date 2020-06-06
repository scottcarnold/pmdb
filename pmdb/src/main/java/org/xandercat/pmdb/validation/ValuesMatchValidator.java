package org.xandercat.pmdb.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.xandercat.pmdb.util.Pair;

/**
 * Validator for validating that two values from a pair are equal to each other.  Pair object
 * must be non-null to be considered potentially valid, but objects within the pair can be null.
 * 
 * @author Scott Arnold
 */
public class ValuesMatchValidator implements ConstraintValidator<ValuesMatch, Pair<?>> {

	@Override
	public void initialize(ValuesMatch constraintAnnotation) {
	}

	@Override
	public boolean isValid(Pair<?> value, ConstraintValidatorContext context) {
		return value != null 
				&& ((value.getFirst() == null && value.getSecond() == null)
						|| (value.getFirst() != null && value.getFirst().equals(value.getSecond())));
	}
}
