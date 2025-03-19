package com.example.v2.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.v2.model.LineNotifyErrorResponse;
import com.example.v2.model.LineNotifyRequest;
import com.example.v2.model.LineNotifyResponse;
import com.example.v2.service.LineNotifyService;

//=================================== 
//目的:處理LINE NOTIFY的HTTP請求
//1. 符合單一職責原則:專注處理HTTP請求
//2. 輸入內容進行驗證:使用者打過來的內容會進行初步的驗證，不浪費資源
//3. 錯誤處理:LineNotifyErrorResponse裡面包含了(自訂義錯誤碼,錯誤類型,具體錯誤原因)這三個
//4. 如果今天使用者發生問題可以直接透過定義的狀態碼去了解到哪邊出了問題，並且透過時間去查詢LOG，快速定義錯誤
//===================================

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/linenotifyV2")
public class LineNotifyController {

	private static final Logger logger = LogManager.getLogger(LineNotifyController.class);
	private LineNotifyService lineNotifyService = new LineNotifyService();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendNotification(LineNotifyRequest request) {
		logger.info("收到 LINE 通知請求");

//		  =================================== 
//		  先進行條件判斷，不行就踢回去
//		  ===================================
		if (request == null) {
			logger.warn("請求體為空");
			return buildLineNotifyErrorResponse(Response.Status.BAD_REQUEST, "LN001", "INVALID_REQUEST", "請求體不能為空");
		}
		if (request.getToken() == null || request.getToken().isEmpty()) {
			logger.warn("Token 無效或為空");
			return buildLineNotifyErrorResponse(Response.Status.BAD_REQUEST, "LN002", "INVALID_TOKEN", "Token 不能為空");
		}
		if (request.getMessage() == null || request.getMessage().isEmpty()) {
			logger.warn("消息內容無效或為空");
			return buildLineNotifyErrorResponse(Response.Status.BAD_REQUEST, "LN003", "INVALID_MESSAGE", "消息內容不能為空");
		}

		logger.info("準備發送 LINE Notify 通知，Token{}Message {}", request.getToken(), request.getMessage());

		// 進行業務邏輯在service，並用Object類型去接收(方便接收到同類型)
		Object result = lineNotifyService.sendLineNotify(request.getToken(), request.getMessage());

//		  =================================== 
//		  根據service結果進行條件判斷
//		  假如回的是LineNotifyResponse為成功，回LineNotifyErrorResponse為失敗 
//		  例外情況回"LN999","UNKNOWN_ERROR" 
//		  ===================================

		if (result instanceof LineNotifyResponse) {
			LineNotifyResponse response = (LineNotifyResponse) result;
			logger.info("LINE 通知發送成功");
			return Response.ok(response).build();

		} else if (result instanceof LineNotifyErrorResponse) {
			LineNotifyErrorResponse error = (LineNotifyErrorResponse) result;
			logger.error("發送 LINE 通知時發生錯誤: {} - {}", error.getErrorCode(), error.getErrorMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
		} else {
			logger.error("發送 LINE 通知時發生未知錯誤");
//			return buildLineNotifyErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "LN999", "UNKNOWN_ERROR",
//					"發送 LINE 通知時發生未知錯誤");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new LineNotifyErrorResponse("LN999", "UNKNOWN_ERROR", "發送 LINE 通知時發生未知錯誤")).build();

		}

	}

// 確保風格統一多寫一個buildLineNotifyErrorResponse(HTTP狀態碼，自訂一錯誤碼，錯誤類型，錯誤訊息)
	private Response buildLineNotifyErrorResponse(Response.Status status, String errorCode, String errorType,
			String errorMessage) {
		LineNotifyErrorResponse error = new LineNotifyErrorResponse(errorCode, errorType, errorMessage);
		return Response.status(status).entity(error).build();
	}

//	@GET
//	@Path("/test")
//	@Produces(MediaType.TEXT_HTML)
//	public Response showTestPage() {
//		return Response.ok().entity("/linenotify-test.jsp").build();
//	}
}