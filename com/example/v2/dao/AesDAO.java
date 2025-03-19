package com.example.v2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.example.v2.model.AesResponse;
import com.example.v2.model.LineNotifyErrorResponse;

public class AesDAO {
	private static final Logger logger = LogManager.getLogger(AesDAO.class);

	
	/**
	 * 將 AES 操作日誌保存到數據庫
	 * 
	 * @param data       原始數據
	 * @param dataResult 操作結果數據
	 * @param iv         初始化向量
	 * @param key        密鑰
	 */
	public static AesResponse logToDatabase(String data, String dataResult, String iv, String key) {
		String trackId = ThreadContext.get("trackId");
		logger.info("開始記錄郵件日誌trackId:{}",trackId);
		
		String sql = "INSERT INTO AES_ENCRYPTION_DECRYPTION1101 (ID, TRACK_ID,data, dataresult, iv, key, timestamp) VALUES (NEXTVAL FOR email_seq01,?,?, ?, ?, ?, ?)";

		try (Connection connection = DBConnection.getConnection();
	         PreparedStatement statement = connection.prepareStatement(sql)) {
			 // 檢查必要的欄位是否為空
            if (trackId == null ) {
                logger.error("必要TrackId欄位為空 - TrackId: {}", trackId);
            }
            
			int paramIndex = 1;
			
			statement.setString(paramIndex++, trackId);
			statement.setString(paramIndex++, data);
			statement.setString(paramIndex++, dataResult);
			statement.setString(paramIndex++, iv);
			statement.setString(paramIndex++, key);
			statement.setTimestamp(paramIndex++, new Timestamp(System.currentTimeMillis()));

			statement.executeUpdate();
			
			if (true) {
			throw new SQLException("發生SQLException");	
		}
			
			logger.info("AES 操作結果已保存到數據庫。");
			return null;   // 返回 null 表示操作成功，因為是返回Error的東西
		} catch (SQLException e) {
			logger.error("保存 AES 操作結果時發生SQL錯誤", e);
			return new AesResponse("AES0088.1", "寫入資料庫發生錯誤");
			//不會影響到整體的業務邏輯
		} catch (Exception e) {
			logger.error("保存 AES 操作結果時發生錯誤", e);
			return new AesResponse("AES0088", "寫入資料庫發生錯誤");
			//不會影響到整體的業務邏輯
		}
	}
}