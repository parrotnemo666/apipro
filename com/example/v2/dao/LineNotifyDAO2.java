package com.example.v2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.Email;

import org.apache.jasper.tagplugins.jstl.core.If;
import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.example.v2.model.EmailRequest;
import com.example.v2.model.EmailResponse;
import com.example.v2.model.LineNotifyErrorResponse;
import com.example.v2.model.EmailRequest.ImageAttachment;
import com.example.v2.service.EmailService;

import com.other.DBConnection;

/**
 * LINE Notify 數據訪問對象 負責處理與 LINE_NOTIFY1 表的所有數據庫操作
 */   
public class LineNotifyDAO2 {
	// 使用 Log4j2 進行日誌記錄
	private static final Logger logger = LogManager.getLogger(LineNotifyDAO2.class);

	// SQL 插入語句，包含所有必要欄位
	private static final String INSERT_SQL = 
"INSERT INTO LINENOTIFY2(id,track_id, token, message, status_code, response_body, timestamp) "
+ "VALUES (NEXTVAL FOR email_seq01,?, ?, ?, ?, ?, ?)";
//LINE_NOTIFY2
	private EmailService emailService;

	public LineNotifyDAO2() {
		this.emailService = new EmailService();

	}

	/**
	 * 將 LINE Notify 的請求記錄保存到數據庫
	 * 
	 * @param token        LINE Notify 的訪問令牌
	 * @param message      發送的消息內容
	 * @param statusCode   API 響應狀態碼
	 * @param responseBody API 響應內容
	 * @return 如果操作成功返回 null，否則返回錯誤響應對象
	 */
	public  LineNotifyErrorResponse logToDatabase(String token, String message, int statusCode, String responseBody) {
		// 使用 try-with-resources 自動管理數據庫資源
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

			// 生成新的ID
			String trackId = ThreadContext.get("trackId");


			 int paramIndex = 1;
			
			// 設置 PreparedStatement 的參數
			
			statement.setString(paramIndex++, trackId); // trackId
			statement.setString(paramIndex++, token); // LINE Notify Token
			statement.setString(paramIndex++, message); // 發送的消息
			statement.setInt(paramIndex++, statusCode); // API 響應狀態碼
			statement.setString(paramIndex++, responseBody); // API 響應內容
			statement.setTimestamp(paramIndex++, new Timestamp(System.currentTimeMillis())); // 當前時間戳

			// 記錄即將執行的 SQL 語句（用於調試）
			logger.debug("執行的SQL語句: {}，trackId: {}", INSERT_SQL, trackId);

			// 執行SQL語句並獲取影響的行數
			int rowsAffected = statement.executeUpdate();
			// 記錄操作結果
			logger.info("API請求已記錄到資料庫，影響 {} 行，trackId: {}", rowsAffected, trackId);

			// 返回 null 表示操作成功
			return null;

		} catch (SQLException e) {
			// 記錄 SQL 異常並返回錯誤響應
			logger.error("保存API請求到資料庫時發生SQL錯誤", e);
			return new LineNotifyErrorResponse("LN501", // 錯誤代碼
					"DATABASE_ERROR", // 錯誤類型
					"數據庫操作失敗：" + e.getMessage() // 錯誤消息
			);
		} catch (Exception e) {
			// 記錄其他異常並返回錯誤響應
			logger.error("保存API請求到資料庫時發生未知錯誤", e);
			return new LineNotifyErrorResponse("LN502", // 錯誤代碼
					"UNKNOWN_DATABASE_ERROR", // 錯誤類型
					"未知的數據庫錯誤：" + e.getMessage() // 錯誤消息
			);
		}
	}
}
