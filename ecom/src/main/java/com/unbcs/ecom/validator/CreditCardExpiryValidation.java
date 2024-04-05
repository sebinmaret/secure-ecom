package com.unbcs.ecom.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreditCardExpiryValidator.class)
public @interface CreditCardExpiryValidation {
	String message() default "Card Expiry is invalid.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}