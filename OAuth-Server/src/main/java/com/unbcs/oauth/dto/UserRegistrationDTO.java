package com.unbcs.oauth.dto;

import org.hibernate.validator.constraints.Length;

import com.unbcs.oauth.validator.ValidPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationDTO {

	@NotBlank(message = "username cannot be empty.")
	@Length(min = 3, max = 35, message = "username is invalid")
	private String username;

	@NotBlank(message = "firstName cannot be empty.")
	@Length(min = 3, max = 35, message = "firstName is invalid")
	private String firstName;

	@NotBlank(message = "lastName cannot be empty.")
	@Length(min = 3, max = 35, message = "lastName is invalid")
	private String lastName;

	@NotBlank(message = "email cannot be empty.")
	@Email(message = "email is invalid.")
	private String email;

	@NotBlank(message = "password cannot be empty.")
	@ValidPassword
	private String password;

	@NotBlank(message = "confirmPassword cannot be empty.")
	@ValidPassword
	private String confirmPassword;

}
