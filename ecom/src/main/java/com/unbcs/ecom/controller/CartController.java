package com.unbcs.ecom.controller;

import java.security.Principal;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unbcs.ecom.dto.AddItemDTO;
import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.dto.ProductDTO;
import com.unbcs.ecom.dto.UserCartDTO;
import com.unbcs.ecom.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Autowired
	CartService cartService;

	@GetMapping("")
	public ResponseEntity<UserCartDTO> getCart(Authentication authentication, Principal principal) {
		return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(cartService.getCart(authentication.getName()));
	}

	@PostMapping("")
	public ResponseEntity<OperationStatus> addItem(@RequestBody AddItemDTO addItemDTO, Authentication authentication,
			Principal principal) {
		if (addItemDTO != null && addItemDTO.getId() != null) {
			OperationStatus status = cartService.addItem(addItemDTO.getId(), authentication.getName());
			status.setStatusCode(201);
			return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(status);
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(
				new OperationStatus("Request body given is null or incomplete.", OperationResultStatus.FAILED, 400));
	}

	@DeleteMapping("/item/{id}")
	public ResponseEntity<OperationStatus> removeItem(@PathVariable String id, Authentication authentication,
			Principal principal) {

		if (id != null && id.length() > 0) {
			ProductDTO productDTO = new ProductDTO();
			productDTO.setId(id);
			OperationStatus status = cartService.removeItem(productDTO.getId(), authentication.getName());
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
		OperationStatus errorResponse = new OperationStatus(OperationResultStatus.FAILED);
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
