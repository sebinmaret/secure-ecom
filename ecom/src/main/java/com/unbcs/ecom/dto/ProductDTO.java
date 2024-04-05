package com.unbcs.ecom.dto;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

	@Nullable
	private String id;

	@Pattern(regexp = "^[a-zA-Z0-9\\-\s\\']{3,30}$", message = "Product Name contains invalid charachters")
	@NotBlank(message = "Product Name cannot be empty.")
	@Length(min = 3, max = 30, message = "Product Name should be between 3 to 30 charachters.")
	private String name;

	@Pattern(regexp = "^[a-zA-Z\\-\s]{3,30}$", message = "Product Category contains invalid charachters")
	@NotBlank(message = "Product Category cannot be empty.")
	@Length(min = 3, max = 30, message = "Product Category should be between 3 to 30 charachters.")
	private String category;

	@NotNull(message = "Product Price cannot be empty")
	@Range(min = 0, max = 100000, message = "Product Price should be between 0 to 100000 in value.")
	private Float price;

	@Pattern(regexp = "^[a-zA-Z0-9\\.\\,\\'\\`\\~\\-\\%\\!\\?\s]{3,300}$", message = "Product Description contains invalid charachters.")
	@NotBlank(message = "Product Description cannot be empty.")
	@Length(min = 3, max = 300, message = "Product Description be between 3 to 300 charachters.")
	private String description;

	@URL(message = "Product imageUrl is not valid.")
	@NotBlank(message = "Product imageUrl cannot be empty.")
	@Length(min = 3, max = 300, message = "Product imageUrl should be between 3 to 50 charachters.")
	private String imageUrl;

}
