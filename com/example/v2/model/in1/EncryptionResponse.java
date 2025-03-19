package com.example.v2.model.in1;

public class EncryptionResponse {

	private String status;
	private String result;
	private String message;

	public EncryptionResponse() {
	}
	public EncryptionResponse(String status,String result,String message) {
		this.status = status;
		this.result = result;
		this.message = message;
	}
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
