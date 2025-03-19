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

import com.example.v2.model.EmailRequest;
import com.example.v2.model.EmailResponse;
import com.example.v2.model.LineNotifyErrorResponse;
import com.example.v2.model.EmailRequest.ImageAttachment;
import com.example.v2.service.EmailService;

import com.other.DBConnection;

/**
 * LINE Notify 數據訪問對象 負責處理與 LINE_NOTIFY1 表的所有數據庫操作
 */
public class LineNotifyDAO {
	// 使用 Log4j2 進行日誌記錄
	private static final Logger logger = LogManager.getLogger(LineNotifyDAO.class);

	// SQL 插入語句，包含所有必要欄位
	private static final String INSERT_SQL = "INSERT INTO LINE_NOTIFY1(id, token, message, status_code, response_body, timestamp) VALUES (?, ?, ?, ?, ?, ?)";

	private EmailService emailService;

	public LineNotifyDAO() {
		this.emailService = new EmailService();

	}

	/**
	 * 生成基於日期的唯一ID 格式: yyyyMMdd-XXXX (例如: 20241018-0001)
	 * 
	 * @return 生成的唯一ID字符串
	 * @throws SQLException 當數據庫操作出錯時拋出
	 */
	private String generateId() throws SQLException {
		// 獲取當前日期
		LocalDate today = LocalDate.now();
		logger.info("獲取當下日期", today);
		// 將日期格式化yyyy-MM-dd從 yyyyMMdd 格式
		String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		logger.info("整理過的日期", datePrefix);

		// 創建用於SQL LIKE查詢的模式
		String pattern = datePrefix + "-%";

		// 使用 try-with-resources 自動關閉數據庫連接和語句
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT MAX(id) FROM LINE_NOTIFY1 WHERE id LIKE ?")) {

			// 設置查詢參數
			stmt.setString(1, pattern);
			// 執行查詢
			ResultSet rs = stmt.executeQuery();

			// 預設序號從1開始
			int sequence = 1;
			if (rs.next()) {
				// 獲取當前日期下的最大ID
				String maxId = rs.getString(1);
				logger.info("獲取當前日期下的最大ID:{}", maxId);
				if (maxId != null) {
					// 從最大ID中提取序號部分（最後4位）
					// maxId.lastIndexOf("-") 找到出現-位置 8，再加一 9
					// 所以從9開始到結尾部分提取出來20241022-0013-> 0013
					String sequenceStr = maxId.substring(maxId.lastIndexOf("-") + 1);
					logger.info(sequenceStr);
					// 將最後查詢到的序號轉換為int並加1
					sequence = Integer.parseInt(sequenceStr) + 1;
					logger.info("將當前日期下的最大ID並+1:{}", sequence);// 4
				}
			}
			logger.info(sequence);
			if (sequence > 9999) {
//				throw new SQLException("序列超過範圍9999，請聯絡管理員");
				return "要超過9999極限拉!";
			}

			if (sequence > 8000) {
				logger.warn("序列號已超過8000，發送警告郵件");
				sendAlertEmail(sequence);
			}

			// 格式化要得新ID：日期前綴 + 四位數序號 EX:20241022-0004
			// %s塞日期、%04d塞編號sequence
			logger.info(String.format("%s-%04d", datePrefix, sequence));
			return String.format("%s-%04d", datePrefix, sequence);
		}
	}

	// 方法 警告LineNotify的序列號超過警告
	private void sendAlertEmail(int sequence) {
		logger.info("已經啟動LineNotify的序列號警告");
		String[] recipients = { "Chun-Yi_Lai@ubot.com.tw" };
		String[] ccRecipients = { "Chun-Yi_Lai@ubot.com.tw" };
		String subject = "LineNotify 序列號警告";
		String message = String.format(
				"警告:LineNotify的序列號警告已達到 %d，已經超過預期警戒值8000  \n" + "請盡快處理以防到達上限9999。 \n" + "發生日期:%s", sequence,
				new java.util.Date()

//			, sequence,LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

		);
		logger.info("LineNotify的序列號警告詳情:{}", message);
		EmailRequest emailRequest = new EmailRequest(recipients, ccRecipients, null, subject, message, null, null);
//			new EmailRequest(recipients, ccRecipients, subject, message)
		Object response = emailService.sendEmail(emailRequest);
		if (response instanceof EmailResponse) {
			logger.info("LN資料庫警告郵件發送成功!");
		} else {
			logger.info("LN資料庫警告郵件發送失敗!");

		}

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
	public LineNotifyErrorResponse logToDatabase(String token, String message, int statusCode, String responseBody) {
		// 使用 try-with-resources 自動管理數據庫資源
		try (Connection connection = DBConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {

			// 生成新的ID
			String newId = generateId();

			if (newId.equals("要超過9999極限拉!")) {
				return new LineNotifyErrorResponse("LN501", // 錯誤代碼
						"DATABASE_ERROR", // 錯誤類型
						"數據庫操作失敗：要超過9999極限拉! "); // 錯誤消息
			}

			// 設置 PreparedStatement 的參數
			statement.setString(1, newId); // ID
			statement.setString(2, token); // LINE Notify Token
			statement.setString(3, message); // 發送的消息
			statement.setInt(4, statusCode); // API 響應狀態碼
			statement.setString(5, responseBody); // API 響應內容
			statement.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // 當前時間戳

			// 記錄即將執行的 SQL 語句（用於調試）
			logger.debug("執行的SQL語句: {}，ID: {}", INSERT_SQL, newId);

			// 執行SQL語句並獲取影響的行數
			int rowsAffected = statement.executeUpdate();
			// 記錄操作結果
			logger.info("API請求已記錄到資料庫，影響 {} 行，日期自訂義ID: {}", rowsAffected, newId);

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
