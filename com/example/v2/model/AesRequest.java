package com.example.v2.model;

/**
 * AES 請求模型類 用於封裝 AES 加密和解密操作的請求參數
 */
public class AesRequest {
	private String data;
	private String iv;
	private String key;
	

	


// Getters and setters  
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
