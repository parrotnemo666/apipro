package com.example.v2.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.example.v2.model.EmailErrorResponse;
import com.example.v2.model.EmailRequest;
import com.example.v2.model.EmailResponse;
import com.other.DBConnection;
public class EmailLogDAOV2 {
    private static final Logger logger = LogManager.getLogger(EmailLogDAOV2.class);
    
    private static final String INSERT_SQL = 
        "INSERT INTO EMAIL_LOG1030 (track_id, recipients, cc_recipients, bcc_recipients, " +
        "subject, has_attachment, status, created_time) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * 記錄郵件發送日誌
     */
    public  EmailErrorResponse logEmail(EmailRequest request, Object response) {
        String trackId = ThreadContext.get("trackId");
        logger.info("開始記錄郵件日誌 - TrackId: {}", trackId);
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
            
            
            pstmt.setString(1, trackId);
            pstmt.setString(2, String.join(",", request.getRecipients()));
            pstmt.setString(3,  String.join(",", request.getCcRecipients()));
            pstmt.setString(4,  String.join(",", request.getBccRecipients()));
            pstmt.setString(5, request.getSubject());
            pstmt.setString(6, 
                request.getAttachments() != null && request.getAttachments().length > 0 ? "Y" : "N");
            
            // 狀態判斷邏輯
            String status;
            if (response instanceof EmailResponse) {
                EmailResponse successResponse = (EmailResponse) response;
                status = successResponse.getStatus();  // 使用實際的狀態
            } else if (response instanceof EmailErrorResponse) {
                status = "FAILED";
            } else {
                status = "UNKNOWN";
                logger.warn("未知的響應類型 - TrackId: {}", trackId);
            }
            pstmt.setString(7, status);
            
            pstmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("郵件日誌保存成功 - TrackId: {}, Status: {}", trackId, status);
                return null;
            } else {
                logger.warn("郵件日誌保存失敗 - TrackId: {}", trackId);
                return new EmailErrorResponse("E502", "DATABASE_ERROR", "無法插入郵件日誌記錄");
            }
            
        } catch (SQLException e) {
            logger.error("數據庫操作錯誤 - TrackId: {}", trackId, e);
            return new EmailErrorResponse("E503", "DATABASE_ERROR", 
                "數據庫操作失敗：" + e.getMessage());
        }
    }
}