package com.moneyTransfer.exception;


/**
 * 基礎交易異常類別 所有交易相關的自定義異常都應該繼承此類
 */
public class TransactionException extends RuntimeException {
	private String errorCode; // 錯誤代碼
	private String errorType; // 錯誤類型
	private String details; // 詳細錯誤信息
	private int severity; // 嚴重程度 (1-5)

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransactionException(String message, String errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public TransactionException(String message, String errorCode, String errorType, int severity) {
		super(message);
		this.errorCode = errorCode;
		this.errorType = errorType;
		this.severity = severity;
	}

// Getters and Setters  
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}
}