package com.model.AES;

public class AesResponse {
	private String encryptedData;
	private String key;
	private String iv;

	// 無參數建構子
	public AesResponse() {
	}

	// 有參數建構子
	public AesResponse(String encryptedData) {
		this.encryptedData = encryptedData;
	}
	// 有參數建構子

	public AesResponse(String encryptedData, String key, String iv) {
		this.encryptedData = encryptedData;
		this.key = key;
		this.iv = iv;
	}

	// Getter 和 Setter 方法
	public String getEncryptedData() {
		return encryptedData;
	}

	public void setEncryptedData(String encryptedData) {
		this.encryptedData = encryptedData;
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
}
