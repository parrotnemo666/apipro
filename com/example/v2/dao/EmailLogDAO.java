package com.example.v2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.v2.model.EmailErrorResponse;
import com.example.v2.model.EmailRequest;
import com.example.v2.model.EmailResponse;
import com.other.DBConnection;
import com.other.EmailConfig;

public class EmailLogDAO {
	private static final Logger logger = LogManager.getLogger(EmailLogDAO.class);
	private static final String INSERT_SQL = "INSERT INTO emailog1001 (id, recipients, subject, status, message_id, sent_time) VALUES (?, ?, ?, ?, ?, ?)";

	// SMTP 配置
	private static final String SMTP_SERVER = EmailConfig.getProperty("smtp.server");
	private static final String SMTP_PORT = EmailConfig.getProperty("smtp.port");
	private static final String SENDER_EMAIL = EmailConfig.getProperty("sender.email");
	private static final String SENDER_PASSWORD = EmailConfig.getProperty("sender.password");

	// 警告郵件接收者
	private static final String[] WARNING_RECIPIENTS = { "Chun-Yi_Lai@ubot.com.tw" };

	/**
	 * 生成基於日期的唯一ID 格式: yyyyMMdd-XXXX (例如: 20241018-0001)
	 */
	private String generateId() throws SQLException {
		LocalDate today = LocalDate.now();
		String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String pattern = datePrefix + "-%";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement("SELECT MAX(id) FROM emailog1001 WHERE id LIKE ?")) {

			stmt.setString(1, pattern);
			ResultSet rs = stmt.executeQuery();
 
			int sequence = 1;
			if (rs.next()) {
				String maxId = rs.getString(1);
				if (maxId != null) {
					String sequenceStr = maxId.substring(maxId.lastIndexOf("-") + 1);
					sequence = Integer.parseInt(sequenceStr) + 1;
					logger.info("當前序列號: {}", sequence);
				}
			}

			if (sequence > 9999) {
				logger.error("序列號超過上限 9999");
				return null;
			}

			if (sequence > 8000) {
				logger.warn("序列號已超過 8000，發送警告郵件");
				sendWarningEmail(sequence);
			}

			return String.format("%s-%04d", datePrefix, sequence);
		}
	}

	/**
	 * 發送警告郵件當序列號超過8000
	 */
	private void sendWarningEmail(int sequence) {
		logger.info("準備發送序列號警告郵件，當前序列號: {}", sequence);

		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP_SERVER);
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		try {
			Session session = Session.getInstance(props);
			Message message = new MimeMessage(session);

			// 設置發件人
			message.setFrom(new InternetAddress(SENDER_EMAIL));

			// 設置收件人
			for (String recipient : WARNING_RECIPIENTS) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			}

			// 設置郵件主題
			message.setSubject("【緊急】Email ID序列號警告");

			// 設置郵件內容
			String warningMessage = String.format(
					"警告：Email ID序列號已達到 %d\n\n" + "詳細信息：\n" + "- 當前序列號：%d\n" + "- 警告閾值：8000\n" + "- 最大限制：9999\n"
							+ "- 產生時間：%s\n\n" + "請儘快處理以防止序列號達到上限。\n" + "建議操作：\n" + "1. 檢查資料庫使用情況\n" + "2. 考慮清理舊記錄\n"
							+ "3. 評估是否需要擴展序列號範圍\n\n" + "此郵件由系統自動發送，請勿直接回覆。",
					sequence, sequence, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

			message.setText(warningMessage);

			// 發送郵件
			Transport transport = session.getTransport("smtp");
			transport.connect(SMTP_SERVER, SENDER_EMAIL, SENDER_PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

			logger.info("序列號警告郵件發送成功");

		} catch (MessagingException e) {
			// 只記錄錯誤但不中斷流程
			logger.error("發送序列號警告郵件時發生錯誤", e);
		} catch (Exception e) {
			// 只記錄錯誤但不中斷流程
			logger.error("發送序列號警告郵件時發生未知錯誤", e);
		}
	}

	/**
	 * 記錄郵件發送日誌
	 */
	public EmailErrorResponse logEmail(EmailRequest request, EmailResponse response) {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

			String newId = generateId();
			if (newId == null) {
				return new EmailErrorResponse("E501", "SEQUENCE_LIMIT_EXCEEDED", "Email ID序列號已超過上限9999");
			}

			pstmt.setString(1, newId);
			pstmt.setString(2, String.join(",", request.getRecipients()));
			pstmt.setString(3, request.getSubject());
			pstmt.setString(4, response.getStatus());
			pstmt.setString(5, response.getMessageId());
			pstmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

			int affectedRows = pstmt.executeUpdate();
			if (affectedRows > 0) {
				logger.info("郵件日誌保存成功，ID: {}", newId);
				return null;
			} else {
				logger.warn("郵件日誌保存失敗");
				return new EmailErrorResponse("E502", "DATABASE_ERROR", "無法插入郵件日誌記錄");
			}
		} catch (SQLException e) {
			logger.error("保存郵件日誌時發生SQL錯誤", e);
			return new EmailErrorResponse("E503", "DATABASE_ERROR", "數據庫操作失敗：" + e.getMessage());
		} catch (Exception e) {
			logger.error("保存郵件日誌時發生未知錯誤", e);
			return new EmailErrorResponse("E504", "UNKNOWN_ERROR", "未知的數據庫錯誤：" + e.getMessage());
		}
	}
}