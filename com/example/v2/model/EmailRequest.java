package com.example.v2.model;

import java.util.Arrays;

public class EmailRequest {
    private String[] recipients;          // 主要收件人
    private String[] ccRecipients;        // CC收件人
    private String[] bccRecipients;       // BCC收件人
    private String subject;               // 主旨
    private String textbody;              // 純文本內容
    private String htmlbody;              // HTML內容
    private ImageAttachment[] attachments; // 新增: 圖片附件

    // 內部類來處理圖片附件
    public static class ImageAttachment {
        private String fileName;    // 檔案名稱
        private String imageData;   // 圖片二進制數據
        private String contentType; //"image/jpeg", "image/png")

        public ImageAttachment(String fileName, String imageData, String contentType) {
            this.fileName = fileName;
            this.imageData = imageData;
            this.contentType = contentType;
        }
        
        public ImageAttachment() {

        }

        // Getters and Setters
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getImageData() {
            return imageData;
        }

        public void setImageData(String imageData) {
            this.imageData = imageData;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }

    // 構造函數
    public EmailRequest() {
    }
    

    
    // 全參數構造函數
    public EmailRequest(String[] recipients, String[] ccRecipients, String[] bccRecipients, 
                       String subject, String textbody, String htmlbody, 
                       ImageAttachment[] attachments) {
        this.recipients = recipients;
        this.ccRecipients = ccRecipients;
        this.bccRecipients = bccRecipients;
        this.subject = subject;
        this.textbody = textbody;
        this.htmlbody = htmlbody;
        this.attachments = attachments;
    }

    // Getters and Setters
    public String[] getRecipients() {
        return recipients;
    }

    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }

    public String[] getCcRecipients() {
        return ccRecipients;
    }

    public void setCcRecipients(String[] ccRecipients) {
        this.ccRecipients = ccRecipients;
    }

    public String[] getBccRecipients() {
        return bccRecipients;
    }

    public void setBccRecipients(String[] bccRecipients) {
        this.bccRecipients = bccRecipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTextbody() {
        return textbody;
    }

    public void setTextbody(String textbody) {
        this.textbody = textbody;
    }

    public String getHtmlbody() {
        return htmlbody;
    }

    public void setHtmlbody(String htmlbody) {
        this.htmlbody = htmlbody;
    }

    public ImageAttachment[] getAttachments() {
        return attachments;
    }

    public void setAttachments(ImageAttachment[] attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "EmailRequest{" +
               "recipients=" + Arrays.toString(recipients) +
               ", ccRecipients=" + Arrays.toString(ccRecipients) +
               ", bccRecipients=" + Arrays.toString(bccRecipients) +
               ", subject='" + subject + '\'' +
               ", textbody='" + textbody + '\'' +
               ", htmlbody='" + htmlbody + '\'' +
               ", attachments=" + (attachments != null ? attachments.length : 0) + " files" +
               '}';
    }
}