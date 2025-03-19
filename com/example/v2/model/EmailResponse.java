package com.example.v2.model;

public class EmailResponse {

	private String status; //回傳狀態
	private String messageId; //每個EMAIL裡面的UUID

	public EmailResponse() {

	}
	
	public EmailResponse(String status,String messageId) {
		this.status = status;
		this.messageId = messageId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}
	
	
}
