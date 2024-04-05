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

import com.unbcs.ecom.dto.FeedbackDTO;
import com.unbcs.ecom.dto.OperationResultStatus;
import com.unbcs.ecom.dto.OperationStatus;
import com.unbcs.ecom.service.FeedbackService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

	@Autowired
	FeedbackService feedbackService;

	@PostMapping("")
	public ResponseEntity<OperationStatus> saveFeedback(@Valid @RequestBody FeedbackDTO feedbackDTO,
			Authentication authentication, Principal principal) {
		if (feedbackDTO != null) {
			OperationStatus status = feedbackService.createFeedback(feedbackDTO);
			if (status.getStatus().compareTo(OperationResultStatus.SUCCESS) == 0) {
				return ResponseEntity.status(HttpStatusCode.valueOf(201)).build();
			}
			return ResponseEntity.status(HttpStatusCode.valueOf(status.getStatusCode())).body(status);
		}

		return ResponseEntity.status(HttpStatusCode.valueOf(400))
				.body(new OperationStatus("Feedback cannot be empty or null", OperationResultStatus.FAILED, 400));

	}

	@GetMapping("")
	public ResponseEntity<List<FeedbackDTO>> getAllFeedback(Authentication authentication, Principal principal) {
		return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(feedbackService.getAllFeedback());

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
