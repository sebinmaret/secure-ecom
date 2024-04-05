package com.unbcs.ecom.dto;

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;

import com.unbcs.ecom.validator.CreditCardExpiryValidation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutDTO {

	@NotBlank(message = "Card Number should not be empty.")
	@CreditCardNumber(message = "Card Number is invalid.")
	private String cardNumber;

	@NotBlank(message = "Card Expiry should not be empty.")
	@CreditCardExpiryValidation(message = "Card Expiry is invalid.")
	private String cardExpiry;

	@NotBlank(message = "Card CVV should not be empty.")
	@Length(min = 3, max = 3, message = "CVV is invalid.")
	@Pattern(regexp = "[0-9][0-9][0-9]", message = "Card CVV is not valid.")
	private String cardCVV;

	@Pattern(regexp = "^[a-zA-Z\\s\\.\\'\\`\\~\\-]{5,75}$", message = "Card CVV is not valid.")
	@NotBlank(message = "Card Holder Name should not be empty.")
	@Length(max = 75, message = "Card Holder Name cannot have more than 70 charachters.")
	private String cardHolderName;

}
