package com.example.v2.strategy;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * AES加密策略實現類
 * 使用AES/CBC/PKCS5Padding模式
 */
public class AESEncryption implements EncryptionStrategy {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    @Override
    public String encrypt(String data, Map<String, String> params) throws Exception {
        // 從參數中獲取密鑰和IV
        byte[] keyBytes = Base64.getDecoder().decode(params.get("key"));
        byte[] ivBytes = Base64.getDecoder().decode(params.get("iv"));
        
        // 創建密鑰規範
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        
        // 初始化加密器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        
        // 執行加密
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String decrypt(String encryptedData, Map<String, String> params) throws Exception {
        // 從參數中獲取密鑰和IV
        byte[] keyBytes = Base64.getDecoder().decode(params.get("key"));
        byte[] ivBytes = Base64.getDecoder().decode(params.get("iv"));
        
        // 創建密鑰規範
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        
        // 初始化解密器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        
        // 執行解密
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
