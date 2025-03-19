package com.model.email;

import java.time.Instant;

public class ErrorResponse {
	private String status; // 錯誤狀態
	private String errorReason; // 錯誤原因
	private String errorMessage; // 詳細錯誤信息
	private String timestamp; // 錯誤發生的時間戳

	public ErrorResponse(String status, String errorReason, String errorMessage) {
		this.status = status;
		this.errorReason = errorReason;
		this.errorMessage = errorMessage;
		this.timestamp = Instant.now().toString();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorReason() {
		return errorReason;
	}

	public void setErrorReason(String errorReason) {
		this.errorReason = errorReason;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

 
}