package com.example.v2.model;

/**
 * AES 響應模型類 用於封裝 AES 加密和解密操作的響應結果
 */
public class AesResponse {
	private String data;
	private String key;
	private String iv;
	private String errorCode;
	private String errorMessage;

// 無參構造函數  
	public AesResponse() {
		
		System.out.println();
	}

// 成功響應的構造函數，正確的時候可以回復這個
	public AesResponse(String data, String key, String iv) {
		this.data = data;
		this.key = key;
		this.iv = iv;
	}
	
	public void add(String data, String key, String iv) {
		this.data = data;
		this.key = key;
		this.iv = iv;
	}

// 錯誤響應的構造函數  
	public AesResponse(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

// Getters and setters  
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}