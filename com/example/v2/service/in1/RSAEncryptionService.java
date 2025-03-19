package com.example.v2.service.in1;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.v2.model.in1.EncryptionRequest;
import com.example.v2.model.in1.EncryptionResponse;
import com.example.v2.strategy.RSAEncryption;

/**
 * RSA加密服務類
 * 提供RSA加密相關的所有操作
 */
public class RSAEncryptionService {
    
    private static final Logger logger = LoggerFactory.getLogger(RSAEncryptionService.class);
    private final RSAEncryption rsaStrategy;
    
    public RSAEncryptionService() {
        this.rsaStrategy = new RSAEncryption();
    }
    
    /**
     * 處理RSA加密請求
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
            logger.error("RSA加密處理失敗: ", e);
            response.setStatus("error");
            response.setMessage(e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 生成RSA密鑰對
     * @return 包含公鑰和私鑰的Map
     */
    public Map<String, String> generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            
            return Map.of(
                "publicKey", publicKey,
                "privateKey", privateKey
            );
        } catch (Exception e) {
            logger.error("生成RSA密鑰對失敗: ", e);
            throw new RuntimeException("生成RSA密鑰對失敗", e);
        }
    }
    
    /**
     * 驗證RSA請求參數
     */
    private void validateRequest(EncryptionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("請求不能為空");
        }
        
        if (StringUtils.isEmpty(request.getData())) {
            throw new IllegalArgumentException("數據不能為空");
        }
        
        Map<String, String> params = request.getParams();
        if (params == null) {
            throw new IllegalArgumentException("RSA參數不能為空");
        }
        
        if ("encrypt".equalsIgnoreCase(request.getOperation()) && !params.containsKey("publicKey")) {
            throw new IllegalArgumentException("RSA加密需要publicKey參數");
        }
        
        if ("decrypt".equalsIgnoreCase(request.getOperation()) && !params.containsKey("privateKey")) {
            throw new IllegalArgumentException("RSA解密需要privateKey參數");
        }
    }
    
    /**
     * 執行RSA加密或解密操作
     */
    private String executeOperation(EncryptionRequest request) throws Exception {
        if ("encrypt".equalsIgnoreCase(request.getOperation())) {
            return rsaStrategy.encrypt(request.getData(), request.getParams());
        } else if ("decrypt".equalsIgnoreCase(request.getOperation())) {
            return rsaStrategy.decrypt(request.getData(), request.getParams());
        } else {
            throw new IllegalArgumentException("不支援的操作類型: " + request.getOperation());
        }
    }
}