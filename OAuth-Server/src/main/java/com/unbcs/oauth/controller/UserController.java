package com.unbcs.oauth.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.unbcs.oauth.dto.ChangeUserPasswordDTO;
import com.unbcs.oauth.dto.OperationResultStatus;
import com.unbcs.oauth.dto.OperationStatus;
import com.unbcs.oauth.dto.UserRegistrationDTO;
import com.unbcs.oauth.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("")
	public ResponseEntity<OperationStatus> registerUser(@Valid @RequestBody UserRegistrationDTO user) {

		OperationStatus status = userService.registerUser(user);
		if (status.getStatus().compareTo(OperationResultStatus.SUCCESS) == 0) {
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(status.getStatusCode())).body(status);

	}

	@PostMapping("/changepassword")
	public ResponseEntity<OperationStatus> changeUserPassword(
			@Valid @RequestBody ChangeUserPasswordDTO changeUserPasswordDTO, Authentication authentication,
			Principal principal) {

		OperationStatus status = userService.changeUserPassword(changeUserPasswordDTO, authentication.getName());
		if (status.getStatus().compareTo(OperationResultStatus.SUCCESS) == 0) {
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(status);
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(status.getStatusCode())).body(status);

	}

	@PostMapping("/admin")
	public ResponseEntity<OperationStatus> registerAdminUser(@Valid @RequestBody UserRegistrationDTO user) {

		OperationStatus status = userService.registerAdminUser(user);
		if (status.getStatus().compareTo(OperationResultStatus.SUCCESS) == 0) {
			return ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
		}
		return ResponseEntity.status(HttpStatusCode.valueOf(status.getStatusCode())).body(status);

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
