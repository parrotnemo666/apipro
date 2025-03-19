package com.example.v2.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;

import com.example.v2.dao.EmailLogDAO;
import com.example.v2.dao.EmailLogDAOV2;
import com.example.v2.dao.EmailLogDAOV3;
import com.example.v2.model.EmailErrorResponse;
import com.example.v2.model.EmailRequest;
import com.example.v2.model.EmailResponse;

import com.other.EmailConfig;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import java.util.Base64;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

public class EmailService {
	private static final Logger logger = LogManager.getLogger(EmailService.class);
	private static final String SMTP_SERVER = EmailConfig.getProperty("smtp.server");
	private static final String SMTP_PORT = EmailConfig.getProperty("smtp.port");
	private static final String SENDER_EMAIL = EmailConfig.getProperty("sender.email");
	private static final String SENDER_PASSWORD = EmailConfig.getProperty("sender.password");

//	private EmailLogDAO emailLogDAO;
//	private EmailLogDAOV2 emailLogDAOV2;
	private EmailLogDAOV3 emailLogDAOV3;

	public EmailService() {
//		this.emailLogDAO = new EmailLogDAO();
		logger.info("初始化 EmailService");
		this.emailLogDAOV3 = new EmailLogDAOV3();
	}
	
	String trackId = ThreadContext.get("trackId");

	public Object sendEmail(EmailRequest request) {
		logger.info("準備發送郵件。收件人: {}, 主題: {}", String.join(", ", request.getRecipients()), request.getSubject());
        logger.debug("郵件配置信息 - SMTP服務器: {}, 端口: {}", SMTP_SERVER, SMTP_PORT);

		
		// 無效的收件人格式會被退回
		if (!validateEmailAddresses(request.getRecipients())) {
			logger.warn("無效的收件人格式");
			return new EmailErrorResponse("E002.1", "INVALID_RECIPIENTS", "收件人格式無效or為空");
		}

		// 無效的CC副本格式會被退回
		if (!validateEmailAddresses(request.getRecipients())) {
			logger.warn("無效的CC收件人格式");
			return new EmailErrorResponse("E002.2", "INVALID_CCRECIPIENTS", "CC格式無效or為空");
		}

		
		
		String trackId = ThreadContext.get("trackId");
        logger.info("當前處理的請求 trackId:{}", trackId);
		// 把SmtpProperties放進session裡面，創建郵件對話
		Session session = Session.getInstance(setupSmtpProperties());
		logger.debug("郵件會話創建成功");
		
		Message message = null;

		try {
			logger.debug("開始創建郵件消息");
			message = createMessage(session, request); // 創建郵件消息
			Transport transport = session.getTransport("smtp"); // 獲取SMTP對象
			
			transport.connect(SMTP_SERVER, SENDER_EMAIL, SENDER_PASSWORD); // 連接到SMTP服務器
			logger.debug("SMTP服務器連接成功");
			 logger.debug("開始發送郵件");
			transport.sendMessage(message, message.getAllRecipients());
			logger.debug("郵件發送完成");
			
			transport.close();

			logger.info("郵件發送成功，生成的 trackId: {}", trackId);

			EmailResponse response = new EmailResponse("SUCCESS", trackId);
			
			// 簡單用來測試exception東西
//			if (true) {
//				throw new MessagingException("發生MessagingException");	
//				throw new AuthenticationFailedException("發生AuthenticationFailedException");
//			}

			// 嘗試記錄到數據庫，但失敗不影響主要流程
			try {
//				emailLogDAO.logEmail(request, response);
				emailLogDAOV3.logEmail(request, response);
				logger.debug("數據庫記錄完成");
			} catch (Exception e) {
				logger.error("數據庫記錄失敗: {}", e.getMessage(), e);
			}

			return response;
		} catch (AuthenticationFailedException e) {
			logger.error("身份驗證錯誤", e);
			return new EmailErrorResponse("E007", "AUTHENTICATION_ERROR", "連接server身份驗證錯誤: " + e.getMessage());

		} catch (MessagingException e) {
			logger.error("郵件發送失敗", e);
			return new EmailErrorResponse("E005", "SEND_FAILED", "郵件建立失敗，郵件發送失敗: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("郵件發送失敗", e);
			return new EmailErrorResponse("E005.1", "SEND_FAILED", "郵件建立失敗，附件BASE64發生錯誤: " + e.getMessage());
		} catch (Exception e) {
			logger.error("發送郵件時發生未知錯誤", e);
			return new EmailErrorResponse("E999", "UNKNOWN_ERROR", "發送郵件時發生未知錯誤: " + e.getMessage());
		}
		
		
	}

	// 檢測email格式是否為正常(普通收件人、CC收件人都可以使用)
	private boolean validateEmailAddresses(String[] addressArrays) {
		String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
		for (String addresses : addressArrays) {
			 logger.debug("驗證郵件地址: {}", addresses);
			if (!Pattern.matches(emailRegex, addresses)) {
				logger.warn("郵件地址格式無效: {}", addresses);
				return false;
			}
		}
		logger.debug("郵件地址格式驗證通過");
		return true;
	}

//SMTP設置(從properties抓參數出來)
	private Properties setupSmtpProperties() {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", SMTP_SERVER);
		properties.put("mail.smtp.port", SMTP_PORT);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
        logger.debug("SMTP屬性設置完成 - 服務器: {}, 端口: {}", SMTP_SERVER, SMTP_PORT);

		return properties;
	}

	// ===================================
// 目的:建立起EMAIL的內容的方法
//	1. Session創建，Message繼承 -> MimeMessage
//	2. MimeMessage包含 -> MimeMultipart
//	3. MimeMultipart包含多個 -> MimeBodyPart
//	4. MimeBodyPart可以是：文本內容、HTML內容、附件
//
//	這結構描述了郵件從Session/Message開始，經MimeMessage和MimeMultipart，
//	最終到MimeBodyPart（文本、HTML、附件）的組成。
	// ===================================
	private Message createMessage(Session session, EmailRequest request) throws MessagingException {
        String trackId = ThreadContext.get("trackId");
        logger.debug("開始創建郵件消息 - TrackId: {}", trackId);

        Message message = new MimeMessage(session);
        logger.debug("設置寄件者信息");
        message.setFrom(new InternetAddress(SENDER_EMAIL));
        message.setSubject(request.getSubject());

        Multipart multipart = new MimeMultipart();
        logger.debug("開始設置收件人信息");

        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(String.join(",", request.getRecipients())));
        logger.debug("主要收件人設置完成");

        if (request.getCcRecipients() != null && request.getCcRecipients().length > 0) {
            logger.debug("設置副本收件人");
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(String.join(",", request.getCcRecipients())));
            logger.debug("副本收件人設置完成");
        }

        if (request.getBccRecipients() != null && request.getBccRecipients().length > 0) {
            String bccList = String.join(",", request.getBccRecipients());
            logger.debug("設置密件副本收件人: {}", bccList);
            try {
                InternetAddress[] bccAddresses = InternetAddress.parse(bccList);
                message.setRecipients(Message.RecipientType.BCC, bccAddresses);
                logger.debug("密件副本收件人設置成功，數量: {}", bccAddresses.length);
            } catch (Exception e) {
                logger.error("設置密件副本收件人時發生錯誤: {}", e.getMessage(), e);
                throw new MessagingException("設置BCC收件人失敗", e);
            }
        }

        logger.debug("開始處理郵件內容");
        if (request.getTextbody() != null && !request.getTextbody().isEmpty()) {
            logger.debug("添加純文本內容");
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(request.getTextbody(), "utf-8");
            multipart.addBodyPart(textPart);
        }

        if (request.getHtmlbody() != null && !request.getHtmlbody().isEmpty()) {
            logger.debug("添加HTML內容");
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(request.getHtmlbody(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
        }

        if (request.getAttachments() != null) {
            logger.debug("開始處理附件，數量: {}", request.getAttachments().length);
            for (EmailRequest.ImageAttachment attachment : request.getAttachments()) {
                try {
                    logger.debug("處理附件: {}", attachment.getFileName());
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    
                    logger.debug("解碼附件Base64數據");
                    byte[] imageBytes = Base64.getDecoder().decode(attachment.getImageData());
                    
                    DataSource source = new ByteArrayDataSource(imageBytes, attachment.getContentType());
                    attachmentPart.setDataHandler(new DataHandler(source));
                    attachmentPart.setFileName(attachment.getFileName());
                    
                    multipart.addBodyPart(attachmentPart);
                    logger.debug("附件 {} 添加成功", attachment.getFileName());
                    
                } catch (IllegalArgumentException e) {
                    logger.error("附件Base64解碼失敗: {}", e.getMessage());
                    throw new IllegalArgumentException("Email Base64發生錯誤");
                } catch (Exception e) {
                    logger.error("處理附件時發生錯誤: {}", e.getMessage());
                    throw new MessagingException("附件處理失敗", e);
                }
            }
        }

        message.setContent(multipart);
        logger.debug("郵件消息創建完成");
        return message;
    }
}