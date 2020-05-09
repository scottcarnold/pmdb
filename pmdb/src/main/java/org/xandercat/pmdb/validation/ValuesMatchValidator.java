package org.xandercat.pmdb.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.xandercat.pmdb.util.Pair;

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
