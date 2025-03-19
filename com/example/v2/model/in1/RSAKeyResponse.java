package com.example.v2.model.in1;

public class RSAKeyResponse {

	private String publicKey;
	private String privateKey;

	public RSAKeyResponse() {

	}

	public RSAKeyResponse(String publicKey, String privateKey) {
		this.publicKey = privateKey;
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

}
