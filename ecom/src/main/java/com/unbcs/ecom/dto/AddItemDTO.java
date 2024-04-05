package com.unbcs.ecom.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.UUID;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddItemDTO {

	@UUID(message = "Product Id is not valid UUID")
	@NotBlank(message = "Product Id cannot be empty")
	@Length(max = 36,min = 36,message = "Invalid Product Id")
	private String id;

}
