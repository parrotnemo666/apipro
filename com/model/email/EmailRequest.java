package com.model.email;

import java.util.List;

public class EmailRequest {
	// 收件人清單
	private String[] recipients;
	// 郵件標題
	private String subject;
	// 純文字內容
	private String textBody;
	// HTML 內容
	private String htmlBody;
	// 附件列表
	private List<Attachment> attachments;

	public EmailRequest() {

	}

	public EmailRequest(String[] recipients, String subject, String textBody, String htmlBody) {
		this.recipients = recipients;
		this.subject = subject;
		this.textBody = textBody;
		this.htmlBody = htmlBody;
	}

	// Getter 和 Setter 方法
	public String[] getRecipients() {
		return recipients;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTextBody() {
		return textBody;
	}

	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
}
