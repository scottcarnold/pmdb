package org.xandercat.pmdb.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValuesMatchValidator.class)
public @interface ValuesMatch {
	String message() default "{validation.ValuesMatch.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
