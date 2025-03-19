package com.moneyTransfer.exception;

/**
 * 驗證異常 用於處理所有與數據驗證相關的異常
 */
public class ValidationException extends TransactionException {
	private String field; // 驗證失敗的欄位
	private String constraint; // 違反的約束條件

	public ValidationException(String message) {
		super(message, "E002", "VALIDATION", 3);
	}

	public ValidationException(String message, String field, String constraint) {
		super(message, "E002", "VALIDATION", 3);
		this.field = field;
		this.constraint = constraint;
	}

	public String getField() {
		return field;
	}

	public String getConstraint() {
		return constraint;
	}
}
