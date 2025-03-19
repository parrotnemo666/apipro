package com.example.v2.service.in1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.v2.model.in1.EncryptionRequest;
import com.example.v2.model.in1.EncryptionResponse;
import com.example.v2.strategy.AESEncryption;

import javax.crypto.KeyGenerator;
import java.security.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * AES加密服務類 提供AES加密相關的所有操作
 */
public class AESEncryptionService {

	private static final Logger logger = LoggerFactory.getLogger(AESEncryptionService.class);
	private final AESEncryption aesStrategy;

	public AESEncryptionService() {
		this.aesStrategy = new AESEncryption();
	}
	
	/**
	 * 執行AES加密或解密操作
	 */
	private String executeOperation(EncryptionRequest request) throws Exception {
		if ("encrypt".equalsIgnoreCase(request.getOperation())) {
			return aesStrategy.encrypt(request.getData(), request.getParams());
		} else if ("decrypt".equalsIgnoreCase(request.getOperation())) {
			return aesStrategy.decrypt(request.getData(), request.getParams());
		} else {
			throw new IllegalArgumentException("不支援的操作類型: " + request.getOperation());
		}
	}

	/**
	 * 處理AES加密請求
	 * 
	 * @param request 加密請求對象
	 * @return 加密響應對象
	 */
	public EncryptionResponse processRequest(EncryptionRequest request) {
		EncryptionResponse response = new EncryptionResponse();

		try {
			validateRequest(request);

			String result = executeOperation(request);
			response.setStatus("success");
			response.setResult(result);
			

		} catch (Exception e) {
			logger.error("AES加密處理失敗: ", e);
			response.setStatus("error");
			response.setMessage(e.getMessage());
		}

		return response;
	}

	/**
	 * 生成AES密鑰和向量
	 * 
	 * @return 包含密鑰和IV的Map
	 */
	public Map<String, String> generateKeyAndIV() {
		try {
			byte[] key = new byte[16]; // 128位密鑰
			byte[] iv = new byte[16]; // 128位初始化向量

			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(key);
			secureRandom.nextBytes(iv);

			return Map.of("key", Base64.getEncoder().encodeToString(key), "iv", Base64.getEncoder().encodeToString(iv));
		} catch (Exception e) {
			logger.error("生成AES密鑰失敗: ", e);
			throw new RuntimeException("生成AES密鑰失敗", e);
		}
	}

	/**
	 * 驗證AES請求參數
	 */
	private void validateRequest(EncryptionRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("請求不能為空");
		}

		if (StringUtils.isEmpty(request.getData())) {
			throw new IllegalArgumentException("數據不能為空");
		}

		Map<String, String> params = request.getParams();
		if (params == null || !params.containsKey("key") || !params.containsKey("iv")) {
			throw new IllegalArgumentException("AES加密需要key和iv參數");
		}
	}

	
}
