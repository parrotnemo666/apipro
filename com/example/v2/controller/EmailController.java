	package com.example.v2.controller;
	
	import javax.ws.rs.*;
	import javax.ws.rs.core.MediaType;
	import javax.ws.rs.core.Response;
	import org.apache.logging.log4j.LogManager;
	import org.apache.logging.log4j.Logger;
	
	import com.example.v2.model.EmailErrorResponse;
	import com.example.v2.model.EmailRequest;
	import com.example.v2.model.EmailResponse;
	import com.example.v2.service.EmailService;
	// ===================================
	//目的:寄送email出去
	//1. 符合單一職責原則:專注處理HTTP請求
	//2. 輸入內容進行驗證:使用者打過來的內容會進行初步的驗證，不浪費資源
	//3. 錯誤處理:LineNotifyErrorResponse裡面包含了(自訂義錯誤碼,錯誤類型,具體錯誤原因)這三個
	//4. 如果今天使用者發生問題可以直接透過定義的狀態碼去了解到哪邊出了問題，並且透過時間去查詢LOG，快速定義錯誤	
	// ===================================
	@Path("/email/v2")
	public class EmailController {
		private static final Logger logger = LogManager.getLogger(EmailController.class);
		private EmailService emailService = new EmailService();

		
		
		@POST
		@Path("/send")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response sendEmail(EmailRequest request) {
			logger.info("收到發送郵件的請求");
	
			if (request == null) {
				logger.warn("請求體為空");
				return buildEmailErrorResponse(Response.Status.BAD_REQUEST, "E001", "INVALID_REQUEST", "請求體不能為空");
			}
			if (request.getRecipients() == null || request.getRecipients().length == 0) {
				logger.warn("收件人列表為空");
				return buildEmailErrorResponse(Response.Status.BAD_REQUEST, "E002", "INVALID_RECIPIENTS", "收件人不能為空");
			}
			if (request.getSubject() == null || request.getSubject().isEmpty()) {
				logger.warn("郵件主題為空");
				return buildEmailErrorResponse(Response.Status.BAD_REQUEST, "E003", "INVALID_SUBJECT", "郵件主題不能為空");
			}
			if ((request.getTextbody() == null || request.getTextbody().isEmpty())
					&& (request.getHtmlbody() == null || request.getHtmlbody().isEmpty())) {
				logger.warn("郵件內容為空");
				return buildEmailErrorResponse(Response.Status.BAD_REQUEST, "E004", "INVALID_BODY", "郵件內容不能為空");
			}
			
	
	
			logger.info("準備發送郵件，收件人: {}", String.join(", ", request.getRecipients()));
	
			Object result = emailService.sendEmail(request);
	
			//如果回復EmailResponse代表成功
			if (result instanceof EmailResponse) {
				EmailResponse response = (EmailResponse) result;
				logger.info("郵件發送成功");
				return Response.ok(response).build();
			//如果回復EmailErrorResponse代表失敗，回傳自定義錯誤代碼
			} else if (result instanceof EmailErrorResponse) {
				EmailErrorResponse error = (EmailErrorResponse) result;
				logger.error("發送郵件時發生錯誤: {} - {}", error.getErrorCode(), error.getErrorMessage());
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
			//發生其他錯誤，進行處理
			} else {
				logger.error("發送郵件時發生未知錯誤"); 
				return buildEmailErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "E999", "UNKNOWN_ERROR",
						"發送郵件時發生未知錯誤");
			}
		}
	//統一格式用的
		private Response buildEmailErrorResponse(Response.Status status, String errorCode, String errorType,
				String errorMessage) {
			EmailErrorResponse error = new EmailErrorResponse(errorCode, errorType, errorMessage);
			return Response.status(status).entity(error).build();
		}
	}