package com.example.v2.service;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.example.v2.dao.AesDAO;
import com.example.v2.model.AesResponse;
import com.other.EmailConfig;


public class AesService {
    private static final Logger logger = LogManager.getLogger(AesService.class);

    private static final String AES_ALOGORITHM = EmailConfig.getProperty("AES_ALOGORITHM"); // AES
    private static final String AES_TRANSFORMATION = EmailConfig.getProperty("AES_TRANSFORMATION"); // AES/CBC/PKCS5Padding
    private static final int AES_KEY_SIZE = Integer.parseInt(EmailConfig.getProperty("AES_KEY_SIZE")); // 256
    private static final int AES_IV_SIZE = Integer.parseInt(EmailConfig.getProperty("AES_IV_SIZE")); // 16

    String trackId = ThreadContext.get("trackId");

    public String generateKey() {
        logger.info("開始生成 AES 密鑰");
        logger.debug("正在生成 AES 密鑰...trackid:{}", trackId);
        
        try {
            logger.debug("使用算法:{} 密鑰大小:{}", AES_ALOGORITHM, AES_KEY_SIZE);
            
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALOGORITHM);
            logger.debug("成功獲取 KeyGenerator 實例");
            
            logger.debug("開始初始化密鑰生成器");
            keyGen.init(AES_KEY_SIZE);
            logger.debug("密鑰生成器初始化完成");

            SecretKey secretKey = keyGen.generateKey();
            logger.debug("密鑰生成成功，長度: {} bytes", secretKey.getEncoded().length);
            logger.info("secretKey:{}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
            
            logger.info("密鑰生成並編碼完成");
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());

        } catch (NoSuchAlgorithmException e) {
            logger.error("生成 AES 密鑰時發生錯誤: {}", e.getMessage(), e);
            return "-1";
        } catch (Exception e) {
            logger.error("生成 AES 密鑰時發生未預期錯誤: {}, 錯誤類型: {}", 
                e.getMessage(), e.getClass().getName(), e);
            return "-1";
        }
    }

    public String generateIV() {
        logger.debug("正在生成初始化向量 (IV)...");
        logger.info("開始生成 IV");
        logger.debug("IV 生成配置 - 大小: {} bytes", AES_IV_SIZE);

        try {
            byte[] iv = new byte[AES_IV_SIZE];
            logger.debug("IV數組已初始化");

            new java.security.SecureRandom().nextBytes(iv);
            logger.debug("已使用 SecureRandom 填充 IV");

            String encodedIv = Base64.getEncoder().encodeToString(iv);
            logger.debug("IV Base64 編碼完成");
            
            logger.info("IV生成成功");
            return encodedIv;
        } catch (Exception e) {
            logger.error("生成 IV 時發生錯誤: {}", e.getMessage(), e);
            return "-2";
        }
    }

    public AesResponse encrypt(String data) {
        logger.info("開始進行 AES 加密操作");
        
        String key = generateKey();
        logger.debug("密鑰生成完成");
        
        if (key == "-1") {
            logger.warn("密鑰生成失敗");
            return new AesResponse("AES001.1", "AES生成KEY錯誤");
        }

        String iv = generateIV();
        logger.debug("IV生成完成");
        
        if (iv == "-2") {
            logger.warn("IV生成失敗");
            return new AesResponse("AES001.2", "AES生成IV值錯誤");
        }

        try {
            logger.debug("準備獲取 Cipher 實例，使用轉換模式: {}", AES_TRANSFORMATION);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            
            logger.debug("準備初始化加密參數");
            SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            logger.debug("Cipher 初始化完成");

            byte[] encrypted = cipher.doFinal(data.getBytes());
            logger.debug("加密操作完成，加密後數據長度: {} bytes", encrypted.length);

            String encryptedData = Base64.getEncoder().encodeToString(encrypted);
            logger.debug("加密數據轉換為 Base64 完成");

            logger.debug("準備記錄到數據庫");
            AesDAO.logToDatabase(data, encryptedData, iv, key);
            logger.info("加密操作全部完成");

            return new AesResponse(encryptedData, key, iv);
            
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.error("加密過程中發生算法錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES001", "加密算法錯誤");
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            logger.error("加密過程中發生密鑰錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES002", "無效的密鑰或IV");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("加密過程中發生數據錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES003", "數據加密錯誤");
        } catch (Exception e) {
            logger.error("加密過程中發生未知錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES087", "數據加密錯誤");
        }
    }

    public AesResponse decrypt(String encryptedData, String key, String iv) {
        logger.info("開始進行 AES 解密操作");
        logger.debug("解密參數檢查 - 數據長度: {}", encryptedData != null ? encryptedData.length() : 0);

        try {
            logger.debug("準備獲取 Cipher 實例");
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            
            logger.debug("準備解碼密鑰和IV");
            SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

            logger.debug("初始化 Cipher 解密模式");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            logger.debug("開始解碼加密數據");
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            logger.debug("解密完成，解密後數據長度: {} bytes", decrypted.length);

            String decryptedData = new String(decrypted);
            logger.debug("數據轉換為字符串完成");

            logger.debug("準備記錄到數據庫");
            AesDAO.logToDatabase(encryptedData, decryptedData, iv, key);
            logger.info("解密操作全部完成");

            return new AesResponse(decryptedData, key, iv);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.error("解密過程中發生算法錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES004", "解密算法錯誤");
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            logger.error("解密過程中發生密鑰錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES005", "無效的密鑰或IV");
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            logger.error("解密過程中發生數據錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES006", "數據解密錯誤");
        } catch (Exception e) {
            logger.error("解密過程中發生未知錯誤: {}", e.getMessage(), e);
            return new AesResponse("AES087", "數據加密錯誤");
        }
    }
}
