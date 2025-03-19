package com.moneyTransfer.model;

import java.util.Date;

public class TransactionResult {
	private boolean success; // 交易是否成功
	private String message; // 結果訊息
	private String transactionId; // 交易序號
	private String errorCode; // 錯誤代碼（如果有）
	private Date processTime; // 處理時間

	// 構造函數
	public TransactionResult() {
		this.processTime = new Date();
	}

	// getter 和 setter 方法
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Date getProcessTime() {
		return processTime;
	}

	public void setProcessTime(Date processTime) {
		this.processTime = processTime;
	}
}
