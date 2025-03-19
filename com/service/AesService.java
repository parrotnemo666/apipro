package com.service;

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

import com.dao.AesDAO;
import com.other.EmailConfig;

public class AesService {
	private static final Logger logger = LogManager.getLogger(AesService.class); // 初始化 Logger

	private static final String AES_ALOGORITHM = EmailConfig.getProperty("AES_ALOGORITHM"); // AES
	private static final String AES_TRANSFORMATION = EmailConfig.getProperty("AES_TRANSFORMATION"); // AES/CBC/PKCS5Padding
	private static final int AES_KEY_SIZE = Integer.parseInt(EmailConfig.getProperty("AES_KEY_SIZE")); // 256
	private static final int AES_IV_SIZE = Integer.parseInt(EmailConfig.getProperty("AES_IV_SIZE")); // 16

	// 生成密鑰KEY
	public static String generateKey() {
		String trackId = ThreadContext.get("trackId");
		logger.info("開始生成 AES 密鑰 - TrackId: {}", trackId);
		logger.debug("正在生成 AES 密鑰...trackid:{}", trackId);

		KeyGenerator keyGen = null;
		try {
			logger.debug("使用算法:{} 密鑰大小:{} - TrackId: {}", AES_ALOGORITHM, AES_KEY_SIZE, trackId);
			keyGen = KeyGenerator.getInstance(AES_ALOGORITHM);

		} catch (NoSuchAlgorithmException e) {
			logger.error("生成 AES 密鑰時發生算法錯誤 - TrackId: {}, 錯誤類型: NoSuchAlgorithmException, 錯誤原因: {}", trackId,
					e.getMessage(), e);
			e.printStackTrace();
			return "-1";
		} catch (Exception e) {
			logger.error("生成 AES 密鑰時發生未預期錯誤 - TrackId: {}, 錯誤類型: {}, 錯誤原因: {}", trackId, e.getClass().getName(),
					e.getMessage(), e);
			e.printStackTrace();
			return "-1";
		}

		logger.debug("開始初始化密鑰生成器 - TrackId: {}", trackId);
		keyGen.init(AES_KEY_SIZE); // 256

		logger.debug("開始生成密鑰 - TrackId: {}", trackId);
		SecretKey secretKey = keyGen.generateKey();

		logger.debug("開始進行 Base64 編碼 - TrackId: {}", trackId);
		String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());

		logger.info("AES 密鑰生成成功 - TrackId: {}, 密鑰長度: {} bytes", trackId, secretKey.getEncoded().length);
		return encodedKey;
	}

	// 生成初始化向量IV值
	public static String generateIV() {
		String trackId = ThreadContext.get("trackId");
		logger.info("開始生成 IV - TrackId: {}", trackId);
		logger.debug("正在生成向量(IV) - TrackId: {}", trackId);
		logger.debug("IV 生成配置 - 大小: {} bytes - TrackId: {}", AES_IV_SIZE, trackId);

		byte[] iv = new byte[AES_IV_SIZE];
		logger.debug("使用 SecureRandom 生成隨機數 - TrackId: {}", trackId);
		new java.security.SecureRandom().nextBytes(iv); // 使用SecureRandom類來填充

		logger.debug("開始進行 Base64 編碼 - TrackId: {}", trackId);
		String encodedIv = Base64.getEncoder().encodeToString(iv);

		logger.info("IV 生成成功 - TrackId: {}, IV長度: {} bytes", trackId, iv.length);
		logger.debug("成功生成 IV: {} - TrackId: {}", encodedIv, trackId);
		return encodedIv;
	}

	// AES加密
	public static String encrypt(String data, String key, String iv) {
		String trackId = ThreadContext.get("trackId");
		logger.info("開始 AES 加密操作 - TrackId: {}", trackId);
		logger.debug("加密配置 - 轉換模式: {} - TrackId: {}", AES_TRANSFORMATION, trackId);

		if (data == null || data.isEmpty()) {
			logger.warn("加密數據為空 - TrackId: {}", trackId);
			return "AES000";
		}

		Cipher cipher = null;
		try {
			logger.debug("準備獲取 Cipher 實例 - TrackId: {}", trackId);
			cipher = Cipher.getInstance(AES_TRANSFORMATION);
			logger.debug("成功獲取 Cipher 實例 - TrackId: {}", trackId);
		} catch (NoSuchAlgorithmException e) {
			logger.error("加密過程中發生算法錯誤 - TrackId: {}, 錯誤原因: {}", trackId, e.getMessage(), e);
			e.printStackTrace();
			return "AES001";
		} catch (NoSuchPaddingException e) {
			logger.error("加密過程中發生填充錯誤 - TrackId: {}, 錯誤原因: {}", trackId, e.getMessage(), e);
			e.printStackTrace();
			return "AES002";
		}

		try {
			logger.debug("準備解碼密鑰和IV - TrackId: {}", trackId);
			SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

			logger.debug("準備初始化 Cipher (加密模式) - TrackId: {}", trackId);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			logger.debug("Cipher 初始化成功 - TrackId: {}", trackId);
		} catch (InvalidKeyException e) {
			logger.error("加密過程中發生密鑰錯誤 - TrackId: {}, 錯誤原因: {}", trackId, e.getMessage(), e);
			e.printStackTrace();
			return "AES003";
		} catch (InvalidAlgorithmParameterException e) {
			logger.error("加密過程中發生參數錯誤 - TrackId: {}, 錯誤原因: {}", trackId, e.getMessage(), e);
			e.printStackTrace();
			return "AES004";
		}

		byte[] encrypted = null;
		try {
			logger.debug("準備加密數據 - TrackId: {}, 原始數據長度: {} bytes", trackId, data.getBytes().length);
			encrypted = cipher.doFinal(data.getBytes());
			logger.debug("數據加密成功 - TrackId: {}, 加密後數據長度: {} bytes", trackId, encrypted.length);
		} catch (IllegalBlockSizeException e) {
			logger.error("加密過程中發生區塊大小錯誤 - TrackId: {}, 錯誤原因: {}", trackId, e.getMessage(), e);
			e.printStackTrace();
			return "AES003";
		} catch (BadPaddingException e) {
			logger.error("加密過程中發生填充錯誤 - TrackId: {}, 錯誤原因: {}", trackId, e.getMessage(), e);
			e.printStackTrace();
			return "AES004";
		}

		logger.debug("準備進行 Base64 編碼 - TrackId: {}", trackId);
		String encryptedData = Base64.getEncoder().encodeToString(encrypted);

		logger.debug("準備將加密記錄寫入數據庫 - TrackId: {}", trackId);
		AesDAO.logToDatabase(data, encryptedData, iv, key);
		logger.info("加密操作完成並已記錄 - TrackId: {}", trackId);

		return encryptedData;
	}

	// AES解密
	public static String decryt(String encryptedData, String key, String iv) {
		String trackId = ThreadContext.get("trackId");
		logger.info("開始 AES 解密操作 - TrackId: {}", trackId);
		logger.debug("解密配置 - 轉換模式: {} - TrackId: {}", AES_TRANSFORMATION, trackId);

		if (encryptedData == null || encryptedData.isEmpty()) {
			logger.warn("待解密數據為空 - TrackId: {}", trackId);
			return "AES000";
		}

		Cipher cipher = null;
		try {
			logger.debug("準備獲取 Cipher 實例 - TrackId: {}", trackId);
			cipher = Cipher.getInstance(AES_TRANSFORMATION);
			logger.debug("成功獲取 Cipher 實例 - TrackId: {}", trackId);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			logger.error("解密過程中發生錯誤 - TrackId: {}, 錯誤類型: {}, 錯誤原因: {}", trackId, e.getClass().getName(), e.getMessage(),
					e);
			e.printStackTrace();
			return "AES004";
		}

		try {
			logger.debug("準備解碼密鑰和IV - TrackId: {}", trackId);
			SecretKeySpec secretKeyspec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

			logger.debug("準備初始化 Cipher (解密模式) - TrackId: {}", trackId);
			cipher.init(Cipher.DECRYPT_MODE, secretKeyspec, ivParameterSpec);
			logger.debug("Cipher 初始化成功 - TrackId: {}", trackId);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			logger.error("解密過程中發生錯誤 - TrackId: {}, 錯誤類型: {}, 錯誤原因: {}", trackId, e.getClass().getName(), e.getMessage(),
					e);
			e.printStackTrace();
			return "AES004";
		}

		byte[] decrypted = null;
		try {
			logger.debug("準備解密數據 - TrackId: {}", trackId);
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
			logger.debug("Base64 解碼完成 - TrackId: {}, 解碼後數據長度: {} bytes", trackId, decodedBytes.length);

			decrypted = cipher.doFinal(decodedBytes);
			logger.debug("數據解密成功 - TrackId: {}, 解密後數據長度: {} bytes", trackId, decrypted.length);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			logger.error("解密過程中發生錯誤 - TrackId: {}, 錯誤類型: {}, 錯誤原因: {}", trackId, e.getClass().getName(), e.getMessage(),
					e);
			e.printStackTrace();
			return "AES004";
		}

		String decryptedData = new String(decrypted);

		logger.debug("準備將解密記錄寫入數據庫 - TrackId: {}", trackId);
		AesDAO.logToDatabase(encryptedData, encryptedData, iv, key);
		logger.info("解密操作完成並已記錄 - TrackId: {}", trackId);

		return decryptedData;
	}
}