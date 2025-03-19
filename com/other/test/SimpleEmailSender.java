package com.other.test;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SimpleEmailSender {
    private static final String SMTP_SERVER = "172.16.5.12";
    private static final String SMTP_PORT = "25";
    private static final String SENDER_EMAIL = "Chun-Yi_Lai@ubot.com.tw";
    private static final String SENDER_PASSWORD = "Ubot@1234";

    public static void main(String[] args) {
        // 設置郵件服務器屬性
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "false"); // 如果需要 TLS，設置為 true
        props.put("mail.debug", "true"); // 啟用調試模式

        // 創建 Session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            // 創建郵件消息
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(SENDER_EMAIL)); // 發送給自己進行測試
            message.setSubject("測試郵件");
            message.setText("這是一封來自 Java 程序的測試郵件。");

            // 發送郵件
            System.out.println("開始發送郵件...");
            Transport.send(message);
            System.out.println("郵件發送成功！");

        } catch (MessagingException e) {
            System.out.println("發送郵件時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
}