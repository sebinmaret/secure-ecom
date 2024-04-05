package com.unbcs.ecom.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.dto.ProductDTO;
import com.unbcs.ecom.misc.ProductConverter;
import com.unbcs.ecom.model.Product;
import com.unbcs.ecom.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ProductConverter converter;

	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	public List<Product> getProductByCategory(String category) {
		return productRepository.findAllByCategory(category);
	}

	public Product getProductById(String id) {
		Optional<Product> optProduct = productRepository.findById(id);
		if (optProduct.isPresent()) {
			return optProduct.get();
		}
		return null;

	}

	public OperationStatus createProduct(ProductDTO productDTO) {
		Product product = converter.toProduct(productDTO);
		product.setId(UUID.randomUUID().toString());
		productRepository.save(product);
		return new OperationStatus(OperationResultStatus.SUCCESS);
	}

	public OperationStatus updateProduct(ProductDTO productDTO) {
		// check if dto has id
		Product updatedProduct = converter.toProduct(productDTO);
		Product oldProduct = productRepository.findById(productDTO.getId()).get();// check if id exist
		// check update logic
		productRepository.save(updatedProduct);
		return new OperationStatus(OperationResultStatus.SUCCESS);
	}

	public OperationStatus deleteProduct(String id) {
		Optional<Product> optProduct = productRepository.findById(id);// check if id exist
		if (optProduct.isEmpty()) {
			return new OperationStatus("productId not found.", OperationResultStatus.FAILED, 404);
		}
		productRepository.deleteById(id);
		return new OperationStatus(OperationResultStatus.SUCCESS);
	}

}
