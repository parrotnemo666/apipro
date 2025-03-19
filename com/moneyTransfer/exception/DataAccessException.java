package com.moneyTransfer.exception;

/**
 * 數據訪問異常 用於處理所有與數據庫操作相關的異常
 */
public class DataAccessException extends TransactionException {
	private String sqlState; // SQL狀態碼
	private int vendorCode; // 資料庫供應商錯誤代碼

	public DataAccessException(String message) {
		super(message, "E001", "DATA_ACCESS", 4);
	}

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
		setErrorCode("E001");
		setErrorType("DATA_ACCESS");
		setSeverity(4);
	}

	public DataAccessException(String message, String sqlState, int vendorCode) {
		super(message, "E001", "DATA_ACCESS", 4);
		this.sqlState = sqlState;
		this.vendorCode = vendorCode;
	}

	public String getSqlState() {
		return sqlState;
	}

	public int getVendorCode() {
		return vendorCode;
	}
}
