package com.model.email;

public class EmailResponse {
    // 郵件發送狀態
    private String status;
    // 唯一的郵件識別碼
    private String messageId;

    // 構造函數
    public EmailResponse(String status, String messageId) {
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
