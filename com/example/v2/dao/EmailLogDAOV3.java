package com.example.v2.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;


import com.example.v2.model.EmailErrorResponse;
import com.example.v2.model.EmailRequest;

import com.other.DBConnection;

/**
 * Email 日誌數據訪問對象 V2版本
 * 負責處理與 EMAIL_LOG1105 表的所有數據庫操作
 */
public class EmailLogDAOV3 {
    private static final Logger logger = LogManager.getLogger(EmailLogDAOV3.class);
    
//    private static final String INSERT_SQL = 
//        "INSERT INTO EMAIL_LOG1105 (ID, TRACK_ID, RECIPIENTS, CC_RECIPIENTS, BCC_RECIPIENTS, " +
//        "SUBJECT, HAS_ATTACHMENT, CREATED_TIME) " +
//        "VALUES (NEXTVAL FOR email_seq01, ?, ?, ?, ?, ?, ?, ?)";

    /**
     * 記錄郵件發送日誌到數據庫
     */
    public EmailErrorResponse logEmail(EmailRequest request, Object response) {
        String trackId = ThreadContext.get("trackId");
        logger.info("開始記錄郵件日誌 - TrackId: {}", trackId);
        
        String INSERT_SQL = 
                "INSERT INTO EMAIL_LOG1105 (ID, TRACK_ID, RECIPIENTS, CC_RECIPIENTS, BCC_RECIPIENTS, " +
                "SUBJECT, HAS_ATTACHMENT, CREATED_TIME) " +
                "VALUES (NEXTVAL FOR email_seq01, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
        	
            // 檢查必要的欄位是否為空
            if (trackId == null || request.getRecipients() == null || request.getSubject() == null) {
                logger.error("必要TrackId欄位為空 - TrackId: {}", trackId);
            }

            int paramIndex = 1;
            
            // 設置基本欄位
            pstmt.setString(paramIndex++, trackId);
            pstmt.setString(paramIndex++, String.join(",", request.getRecipients()));
            
            // CC收件人處理
            String ccRecipients = null;
            if (request.getCcRecipients() != null) {
                ccRecipients = String.join(",", request.getCcRecipients());
            }
            pstmt.setString(paramIndex++, ccRecipients);
            
            // BCC收件人處理
            String bccRecipients = null;
            if (request.getBccRecipients() != null) {
                bccRecipients = String.join(",", request.getBccRecipients());
            }
            pstmt.setString(paramIndex++, bccRecipients);
            
            pstmt.setString(paramIndex++, request.getSubject());
            
            // 附件處理
            String hasAttachment = "N";
            if (request.getAttachments() != null && request.getAttachments().length > 0) {
                hasAttachment = "Y";
            }
            pstmt.setString(paramIndex++, hasAttachment);
            
            pstmt.setTimestamp(paramIndex++, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("郵件日誌保存成功 - TrackId: {}", trackId);
                return null;
            } else {
                logger.warn("郵件日誌保存失敗 - TrackId: {}", trackId);
                return new EmailErrorResponse("E502", "DATABASE_ERROR", "無法插入郵件日誌記錄");
            }

        } catch (SQLException e) {
            logger.error("數據庫操作錯誤 - TrackId: {}, SQL State: {}", trackId, e.getSQLState(), e);
            return new EmailErrorResponse("E503", "DATABASE_ERROR", "數據庫操作失敗：" + e.getMessage());
        } catch (Exception e) {
            logger.error("未預期的錯誤 - TrackId: {}", trackId, e);
            return new EmailErrorResponse("E504", "UNEXPECTED_ERROR", "發生未預期的錯誤：" + e.getMessage());
        }
    }
}