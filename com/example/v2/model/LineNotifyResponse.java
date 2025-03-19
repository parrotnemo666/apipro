package com.example.v2.model;

public class LineNotifyResponse {
	private String status;
	private String message;

	public LineNotifyResponse(String status, String message) {
		this.status = status;
		this.message = message;
	}

// Getters  
	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
