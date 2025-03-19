package com.example.v2.strategy;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

/**
 * Hash加密策略實現類
 * 支持多種Hash算法：MD5, SHA-1, SHA-256等
 */
public class HashEncryption implements EncryptionStrategy {
    private static final String DEFAULT_ALGORITHM = "SHA-256";

    @Override
    public String encrypt(String data, Map<String, String> params) throws Exception {
        // 獲取指定的Hash算法，如果未指定則使用默認的SHA-256
//        String algorithm = params != null && params.containsKey("algorithm") 
//                          ? params.get("algorithm") 
//                          : DEFAULT_ALGORITHM;
    	
    	String algorithm;
		if (params != null  && params.containsKey("alogorithm")) {
    		algorithm = params.get("alogorithm");
		} else {
			algorithm = DEFAULT_ALGORITHM;
		}
        
        // 創建消息摘要對象
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        
        // 計算Hash值
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        
        // 返回Base64編碼的Hash值
        return Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public String decrypt(String encryptedData, Map<String, String> params) throws Exception {
        // Hash是單向的，不支持解密
        throw new UnsupportedOperationException("Hash加密是單向的，不支持解密操作");
    }
}