package com.service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Properties;
import java.util.UUID;

import com.dao.EmailLogDAO;
import com.model.email.EmailRequest;
import com.model.email.EmailResponse;
import com.other.EmailConfig;
import com.sun.mail.smtp.SMTPTransport;

////簡化的認證器類，練立連線如果需要(帳號密碼)認證的話需要的
//class SimpleAuthenticator extends Authenticator {
//	private String username;
//	private String password;
//	private static final Logger logger = LogManager.getLogger(SimpleAuthenticator.class);
//
//	public SimpleAuthenticator(String username, String password) {
//		this.username = username;
//		this.password = password;
//	}
//
//	// 重寫getPasswordAuthentication 方法，提供身分驗證訊息
//	@Override
//	protected PasswordAuthentication getPasswordAuthentication() {
//		logger.info("使用發件人郵箱進行身份驗證：{}", username);
//		return new PasswordAuthentication(username, password);
//	}
//}

//使用JAVA MAIL API來發送郵件，支持HTML的內容發送
public class EmailService3 {

	private static final Logger logger = LogManager.getLogger(EmailService3.class);

	// SMTP配置
	private static final String SMTP_SERVER = EmailConfig.getProperty("smtp.server");
	private static final String SMTP_PORT = EmailConfig.getProperty("smtp.port");
	private static final String SENDER_EMAIL = EmailConfig.getProperty("sender.email");
	private static final String SENDER_PASSWORD = EmailConfig.getProperty("sender.password");

	private EmailLogDAO emailLogDAO = new EmailLogDAO();

	public EmailResponse sendEmail(EmailRequest request) {
		// 設置SMTP PROPERTIES 的屬性
		Properties properties = new Properties();
		properties.put("mail.smtp.host", SMTP_SERVER);
		properties.put("mail.smtp.port", SMTP_PORT);
		properties.put("mail.smtp.auth", "true");
//        properties.put("mail.debug", "true"); //這個選項可以看到很多內部細節

		logger.info("準備開始寄信，收件人如下:{}", String.join(",", request.getRecipients()));

		// 創建帶有身分認證的 session getInstance是用來創建Session(配置對象)

//		Authenticator authenticator = new SimpleAuthenticator(SENDER_EMAIL, SENDER_PASSWORD);

		// 創建 Session 實例(如果server需要用到帳號密碼需要authenticator)
//		Session session = Session.getInstance(properties, authenticator);
		Session session = Session.getInstance(properties);
		SMTPTransport transport = null;
		EmailResponse response = null;

		try {
			// 創建郵件對象一個message代表一封郵件 MimeMessage代表message具體實現
			//session裡面包含了服務器設置和認證訊息
			Message message = new MimeMessage(session); 
			message.setFrom(new InternetAddress(SENDER_EMAIL)); // 寄見人
			// InternetAddress.parse可以接受多個以逗號分開的email地址
			// setRecipients(收件人的類型,郵件地址的list)
			message.setRecipients
			(Message.RecipientType.TO,InternetAddress.parse(String.join(",", request.getRecipients())));
			message.setSubject(request.getSubject()); // 主旨

			message.setText("這邊其實也可以直接發送");
			
			// 創建多部分郵件內容
			Multipart multipart = new MimeMultipart();

			// 創建文本內容
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(request.getTextBody(), "utf-8");
			multipart.addBodyPart(textPart);

			// 如果有HTML的話，創建HTML內容
			if (request.getHtmlBody() != null && !request.getHtmlBody().isEmpty()) {
				
				MimeBodyPart htmlPart = new MimeBodyPart();
				htmlPart.setContent(request.getHtmlBody(), "text/html; charset=utf-8");
				multipart.addBodyPart(htmlPart);
			}

			// 將多部分的內容合併成為郵件內容
			message.setContent(multipart);

			// 建立連接 身分驗證 發送郵件
			// 順便把transport轉成SMTPTransport 他們兩個互相為父子類
			transport = (SMTPTransport) session.getTransport("smtp");
			// 連接服務器
			transport.connect(SMTP_SERVER, SENDER_EMAIL, SENDER_PASSWORD);
			//sendMessage(要發送的郵件本身,發送人的郵件地址)
			transport.sendMessage(message, message.getAllRecipients()); // XX
			
			

			logger.info("Email sent successfully.");

			// 下行獲得紀錄
			int responseCode = transport.getLastReturnCode();
			String responseMessage = transport.getLastServerResponse();
			logger.info("Response Code: {}", responseCode);
			logger.info("Response Message: {}", responseMessage);

			// 關閉連接
			transport.close();

			// 產生一組UUID，僅作為後續維護可以查詢
			String messageId = UUID.randomUUID().toString();
			logger.info("Generated messageId: {}", messageId);

			// 回去controller去
			response = new EmailResponse("SUCCESS", messageId); // 回傳給客戶端
			emailLogDAO.logEmail(request, response);

		} catch (MessagingException e) {
			logger.error("Failed to send email: {}", e.getMessage(), e);

			response = new EmailResponse("FAILED", "寄送發生錯誤");
			emailLogDAO.logEmail(request, response);

		} finally {
			if (transport != null && transport.isConnected()) {
				try {
					transport.close();
					logger.debug("SMTP connection closed.");
				} catch (MessagingException e) {
					logger.error("Failed "
							+ " close SMTP connection: {}", e.getMessage(), e);
				}
			}
		}
        //回傳response回去
		return response;
	}

	// 用來簡單進行測試的main方法
	public static void main(String[] args) {
		EmailService3 emailService = new EmailService3();
		EmailRequest request = new EmailRequest();

		// 簡易測試
		request.setRecipients(new String[] { "Chun-Yi_Lai@ubot.com.tw" });
		request.setSubject("Test Email");
		request.setTextBody("This is a test email from EmailService.");
		request.setHtmlBody("<h1>Test Email</h1><p>This is a <strong>test email</strong> from EmailService.</p>");

		try {
			EmailResponse response = emailService.sendEmail(request);
			System.out.println("Email sent with status: " + response.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
