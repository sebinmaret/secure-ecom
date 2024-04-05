package com.unbcs.ecom.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDTO {

	@Pattern(regexp = "^[a-zA-Z0-9\\.\\,\\'\\`\\~\\-\\_\\!\\?\\$\\%\\(\\)\s]{3,300}$", message = "Description contains invalid charachters.")
	@NotBlank(message = "Description cannot be blank.")
	@Length(min = 3,max = 300,message = "Length of description is out of allowed limits")
	private String description;

	@NotBlank(message = "Email cannot be blank.")
	@Email(message = "Email not valid")
	@Length(min = 6,max = 64,message = "Length of email is out of allowed limits 6-64 charachters")
	private String email;

	@NotBlank(message = "Name cannot be blank.")
	@Length(min = 3,max = 75,message = "Length of name is out of allowed limits 3-75 charachters")
	private String name;
	
	@Nullable
	private String date;

}
