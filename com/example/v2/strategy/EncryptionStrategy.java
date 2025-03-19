package com.example.v2.strategy;


import java.util.Map;

/**
 * 加密策略介面
 * 定義所有加密策略必須實現的方法
 */
public interface EncryptionStrategy {
    /**
     * 加密方法
     * @param data 要加密的數據
     * @param params 加密參數
     * @return 加密後的數據
     */
    String encrypt(String data, Map<String, String> params) throws Exception;

    /**
     * 解密方法
     * @param encryptedData 要解密的數據
     * @param params 解密參數
     * @return 解密後的數據
     */
    String decrypt(String encryptedData, Map<String, String> params) throws Exception;
}