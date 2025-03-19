package com.example.v2.model;

public class LineNotifyErrorResponse {
	private String errorCode; //自訂義錯誤號碼
	private String errorType; //自訂義錯誤類型
	private String errorMessage; //自訂義錯誤訊息

	public LineNotifyErrorResponse(String errorCode, String errorType, String errorMessage) {
		this.errorCode = errorCode;
		this.errorType = errorType;
		this.errorMessage = errorMessage;
	}

// Getters  
	public String getErrorCode() {
		return errorCode;
	}

	public String getErrorType() {
		return errorType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}