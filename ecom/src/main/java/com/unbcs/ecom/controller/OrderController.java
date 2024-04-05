package com.unbcs.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unbcs.ecom.dto.CheckoutDTO;
import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.dto.OrderDTO;
import com.unbcs.ecom.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	OrderService orderService;

	@PostMapping("/checkout")
	public ResponseEntity<OperationStatus> checkout(@Valid @RequestBody CheckoutDTO checkoutDTO,
			Authentication authentication, Principal principal) {

		OperationStatus status = orderService.createOrder(authentication.getName(), checkoutDTO);
		return ResponseEntity.status(HttpStatusCode.valueOf(status.getStatusCode())).body(status);

	}

	@GetMapping("/")
	public ResponseEntity<List<OrderDTO>> getAllOrders(Authentication authentication, Principal principal) {

		List<OrderDTO> orders = orderService.getOrderList(authentication.getName());
		return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(orders);

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
