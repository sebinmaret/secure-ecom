package com.unbcs.oauth.dto;

import org.hibernate.validator.constraints.Length;

import com.unbcs.oauth.validator.ValidPassword;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeUserPasswordDTO {
	
	@NotBlank(message = "oldPassword cannot be empty.")
	@Length(min = 2, max = 64, message = "oldPassword is invalid")
	private String oldPassword;

	@ValidPassword
	@NotBlank(message = "password cannot be empty.")
	@Length(min = 8, max = 64, message = "password is invalid")
	private String password;

	@ValidPassword
	@NotBlank(message = "confimPassword cannot be empty.")
	@Length(min = 8, max = 64, message = "confimPassword is invalid")
	private String confirmPassword;

}
