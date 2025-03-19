package com.example.v2.model;

public class LineNotifyRequest {
	private String token; //使用者已經申請好的token
	private String message; //使用者想要傳送的訊息

// Getters and setters  
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
