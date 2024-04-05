package com.unbcs.ecom.misc;

import org.springframework.stereotype.Service;

import com.unbcs.ecom.dto.ProductDTO;
import com.unbcs.ecom.model.Product;

@Service
public class ProductConverter {

	public Product toProduct(ProductDTO productDto) {
		return new Product(productDto.getId(), productDto.getName(), productDto.getCategory(), productDto.getPrice(),
				productDto.getDescription(), productDto.getImageUrl());
	}

	public ProductDTO toProductDTO(Product product) {
		return new ProductDTO(product.getId(), product.getName(), product.getCategory(), product.getPrice(),
				product.getDescription(), product.getImageUrl());
	}
}
