package com.unbcs.ecom.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.dto.ProductDTO;
import com.unbcs.ecom.model.Product;
import com.unbcs.ecom.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {

	@Autowired
	ProductService productService;

	@GetMapping("")
	public ResponseEntity<List<Product>> getProducts( @RequestParam(name = "category", required = false) String category,
			Authentication authentication, Principal principal) {

		if (category != null && category.length() > 0) {
			return ResponseEntity.status(HttpStatusCode.valueOf(200))
					.body(productService.getProductByCategory(category));
		} else if (category == null) {
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(productService.getAllProducts());
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable(name = "id") String id) {
		if (id != null && id.length() > 0) {
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(productService.getProductById(id));
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
	}

	@PostMapping("")
	public ResponseEntity<OperationStatus> createProduct(@Valid @RequestBody ProductDTO productDTO) {
		if (productDTO != null) {
			OperationStatus status = productService.createProduct(productDTO);
			status.setStatusCode(201);
			return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(status);
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(
				new OperationStatus("Request body given is null or incomplete.", OperationResultStatus.FAILED, 400));
	}

	@PutMapping("/{id}")
	public ResponseEntity<OperationStatus> updateProduct(@PathVariable(name = "id") String id,
			@RequestBody ProductDTO productDTO) {
		if (productDTO != null && id != null && id.length() > 0) {
			productDTO.setId(id);
			OperationStatus status = productService.updateProduct(productDTO);
			status.setStatusCode(201);
			return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(status);
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(
				new OperationStatus("Request body given is null or incomplete.", OperationResultStatus.FAILED, 400));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<OperationStatus> deleteProduct(@PathVariable(name = "id") String id) {
		if (id != null && id.length() > 0) {
			OperationStatus status = productService.deleteProduct(id);
			if (status.getStatus().compareTo(OperationResultStatus.SUCCESS) == 0) {
				return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(status);
			}
			return ResponseEntity.status(HttpStatusCode.valueOf(status.getStatusCode())).body(status);

		}
		return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(
				new OperationStatus("Request body given is null or incomplete.", OperationResultStatus.FAILED, 400));

	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<OperationStatus> handleValidationExceptions(MethodArgumentNotValidException ex) {
		OperationStatus errorResponse=new OperationStatus(OperationResultStatus.FAILED);
		errorResponse.setIsMessage(true);
		errorResponse.setMessage("");
		errorResponse.setStatusCode(400);
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errorResponse.setMessage(errorResponse.getMessage() + fieldName +" - "+errorMessage + "; ");
			
		});
		return ResponseEntity.status(HttpStatusCode.valueOf(errorResponse.getStatusCode())).body(errorResponse);
	}

}
