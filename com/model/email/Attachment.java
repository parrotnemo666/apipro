package com.model.email;

//未來再加入的的功能
public class Attachment {
    // 附件的檔案名稱
    private String filename;
    // 附件的內容類型 (例如：application/pdf, image/png)
    private String contentType;
    // 附件的內容，使用 Base64 編碼
    private String content;

    // Getter 和 Setter 方法
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

