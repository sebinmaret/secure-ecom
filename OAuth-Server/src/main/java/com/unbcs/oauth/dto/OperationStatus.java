package com.unbcs.oauth.dto;

import lombok.Data;

@Data
public class OperationStatus {

	private String message;
	private OperationResultStatus status;
	private Boolean isMessage;
	private Integer statusCode;

	public OperationStatus(String message, OperationResultStatus status, Integer statusCode) {
		this.isMessage = true;
		this.message = message;
		this.status = status;
		this.statusCode = statusCode;
	}

	public OperationStatus(OperationResultStatus status, Integer statusCode) {
		this.isMessage = false;
		this.message = "";
		this.status = status;
		this.statusCode = statusCode;
	}

	public OperationStatus(OperationResultStatus status) {
		this.isMessage = false;
		this.message = "";
		this.status = status;
		this.statusCode = 0;
	}

}
