package com.unbcs.ecom.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CreditCardExpiryValidator implements ConstraintValidator<CreditCardExpiryValidation, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
		simpleDateFormat.setLenient(false);
		Date expiry;
		try {
			expiry = simpleDateFormat.parse(value);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		boolean expired = expiry.before(new Date());
		

		return !expired;
		// Implement your validation logic here
		// Return true if the value is valid, false otherwise
	}
}