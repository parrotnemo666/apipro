//package com.service;
//import com.model.email.EmailRequest;
//import com.model.email.EmailResponse;
//
//
//
//import java.util.Arrays;
//import java.util.Base64;
//
//public class EmailServiceTest {
//    public static void main(String[] args) {
//        // 創建 EmailService2 實例
//        EmailService2 emailService = new EmailService2();
//
//        try {
//            // 創建 EmailRequest 對象
//            EmailRequest request = new EmailRequest();
//
//            // 設置收件人
////            request.setRecipients(Arrays.asList("pigtown88@gmail.com"));
//            request.setRecipients(Arrays.asList("Chun-Yi_Lai@ubot.com.tw"));
//            // 設置主題
//            request.setSubject("測試郵件");
//
//            // 設置純文本內容
//            request.setTextBody("這是一封測試郵件的純文本內容。");
//
//            // 設置 HTML 內容
//            request.setHtmlBody("<html><body><h1>測試郵件</h1><p>這是一封測試郵件的 HTML 內容。</p></body></html>");
//
////            // 添加附件（可選）
////            Attachment attachment = new Attachment();
////            attachment.setFilename("test.txt");
////            attachment.setContentType("text/plain");
////            attachment.setContent(Base64.getEncoder().encodeToString("這是附件內容".getBytes()));
////            request.setAttachments(Arrays.asList(attachment));
//
//            // 發送郵件
//            EmailResponse response = emailService.sendEmail(request);
//
//            // 輸出結果
//            System.out.println("郵件發送結果: " + response.getStatus());
//            System.out.println("郵件 ID: " + response.getMessageId());
//
//        } catch (Exception e) {
//            System.err.println("發送郵件時發生錯誤: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}