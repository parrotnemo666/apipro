package com.example.v2.service;

import com.example.v2.model.in1.EncryptionRequest;
import com.example.v2.model.in1.EncryptionResponse;
import com.example.v2.model.in1.RSAKeyResponse;

public interface EncryptionService {
	/**
	 * 處理加密/解密請求
	 * 
	 * @param request 包含加密類型、操作、數據和參數的請求對象
	 * @return 加密/解密結果
	 */
	EncryptionResponse processEncryption(EncryptionRequest request);

	/**
	 * 生成RSA密鑰對
	 * 
	 * @return RSA公鑰和私鑰
	 */
	RSAKeyResponse generateRSAKeyPair() throws Exception;

	/**
	 * 驗證密鑰格式
	 * 
	 * @param key  密鑰字符串
	 * @param type 加密類型
	 * @return 是否有效
	 */
	boolean validateKey(String key, String type);
}