package com.example.v2.model;

public class EmailErrorResponse {

	private String errorCode;
	private String errroeType;
	private String errorMessage;

	public EmailErrorResponse() {

	}
	
	public EmailErrorResponse(String errorCode, String errroeType, String errorMessage) {
		this.errorCode = errorCode;
		this.errroeType = errroeType;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}	

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrroeType() {
		return errroeType;
	}

	public void setErrroeType(String errroeType) {
		this.errroeType = errroeType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
