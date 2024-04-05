package com.unbcs.ecom.dto;

import java.util.List;

import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCartDTO {

	@Nullable
	private String userId;

	@Nullable
	private List<ProductDTO> products;

	@Nullable
	private Float cartTotal;

}
