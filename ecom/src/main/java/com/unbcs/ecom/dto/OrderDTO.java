package com.unbcs.ecom.dto;

import java.util.List;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

	@Nullable
	private String id;

	@Nullable
	private String userId;

	@Nullable
	private List<ProductDTO> products;

	@Nullable
	private Float cartTotal;

}
