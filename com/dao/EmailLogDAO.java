package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.v2.model.EmailErrorResponse;
import com.example.v2.model.LineNotifyErrorResponse;
import com.model.email.EmailRequest;
import com.model.email.EmailResponse;
import com.other.DBConnection;

public class EmailLogDAO {
	
	private static final Logger logger = LogManager.getLogger(EmailLogDAO.class);
	
	public  EmailErrorResponse logEmail(EmailRequest request,EmailResponse response) {
		
		 String sql = "INSERT INTO emailog1001 (recipients, subject, status, message_id, sent_time) VALUES (?, ?, ?, ?, ?)";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement pstmt = conn.prepareStatement(sql)) {

	            pstmt.setString(1, String.join(",", request.getRecipients()));
	            pstmt.setString(2, request.getSubject());
	            pstmt.setString(3, response.getStatus());
	            pstmt.setString(4, response.getMessageId());
	            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

	            int affectedRows = pstmt.executeUpdate();
	            if (affectedRows > 0) {
	                logger.info("Email log saved successfully");
	            } else {
	                logger.warn("Failed to save email log");
	            }
	            return null; // 返回 null 表示操作成功，因為是返回ErrorResponse
	        } catch (SQLException e) {
	            logger.error("Error saving email log: {}", e.getMessage(), e);
	            return new EmailErrorResponse("LN501", "DATABASE_ERROR", "數據庫操作失敗：" + e.getMessage());
	        }
	    }
	}