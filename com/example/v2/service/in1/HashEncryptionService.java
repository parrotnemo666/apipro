package com.example.v2.service.in1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.v2.model.in1.EncryptionRequest;
import com.example.v2.model.in1.EncryptionResponse;
import com.example.v2.strategy.HashEncryption;

/**
 * Hash加密服務類
 * 提供Hash計算相關的所有操作
 */
public class HashEncryptionService {
    
    private static final Logger logger = LoggerFactory.getLogger(HashEncryptionService.class);
    private final HashEncryption hashStrategy;
    
    public HashEncryptionService() {
        this.hashStrategy = new HashEncryption();
    }
    
    /**
     * 處理Hash計算請求
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
            logger.error("Hash計算失敗: ", e);
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 驗證Hash請求參數
     */
    private void validateRequest(EncryptionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("請求不能為空");
        }
        
        if (StringUtils.isEmpty(request.getData())) {
            throw new IllegalArgumentException("數據不能為空");
        }
        
        // 驗證Hash算法（如果指定）
        Map<String, String> params = request.getParams();
        if (params != null && params.containsKey("algorithm")) {
            try {
                MessageDigest.getInstance(params.get("algorithm"));
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalArgumentException("不支援的Hash算法: " + params.get("algorithm"));
            }
        }
        
        if (!"encrypt".equalsIgnoreCase(request.getOperation())) {
            throw new IllegalArgumentException("Hash只支援加密操作");
        }
    }
    
    /**
     * 執行Hash計算操作
     */
    private String executeOperation(EncryptionRequest request) throws Exception {
        return hashStrategy.encrypt(request.getData(), request.getParams());
    }
    
    /**
     * 獲取支援的Hash算法列表
     * @return Hash算法列表
     */
    public List<String> getSupportedAlgorithms() {
        return Arrays.asList(
            "MD5",
            "SHA-1",
            "SHA-256",
            "SHA-384",
            "SHA-512"
        );
    }
}