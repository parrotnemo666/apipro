package com.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.model.linenotify.LineNotifyRequest;
import com.service.LineNotifyService;

@Path("/linenotify")
public class LinenotifyController {

	private static final Logger logger = LogManager.getLogger(LinenotifyController.class);
	LineNotifyService lineNotifyService =new LineNotifyService();

	@POST
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response sendnitification(LineNotifyRequest request ) {
		
	
		
		try {
			logger.info("已經收到LINE通知請求啦");
			
			if (request.getToken() == null || request.getToken().isEmpty()) {
				logger.warn("Token無效或是空值");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			if (request.getMessage() == null || request.getMessage().isEmpty()) {
				logger.warn("message無效或是空值");
				return Response.status(Response.Status.BAD_REQUEST).build();
			}
			
			String proxyHost = "proxy2.xu06p.com.tw";
		    int proxytPort = 80;
		    
		    logger.info("準備發送linenotify通知，代理:{}，端口{}",proxyHost,proxytPort);
		    
		    //啟動lineNotifyService的方法，對line伺服器傳送要求
		    int statusCode = lineNotifyService.sendLineNotify(request.getToken(), request.getMessage(), proxyHost, proxytPort);
			
		    logger.info("LINE通知發送完成，狀態碼: {}",statusCode);
		    
		 // 根據狀態碼返回不同的響應
            if (statusCode >= 200 && statusCode < 300) {
                return Response.ok("{\"status\":\"success\"}").build();
            } else if (statusCode == 401) {
                logger.error("無效的訪問token，請重新確認。");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Invalid access token\"}")
                        .build();
            } else if (statusCode == 429) {
                logger.error("請求頻率過高，請稍後再嘗試。");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"請求頻率過高\"}")
                        .build();
            }else if (statusCode == 500) {
                logger.error("LineNotify伺服器內部發生錯誤。");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\":\"Invalid LineNotify Server\"}")
						.build();
            }  else {
                logger.error("發送消息失敗，可能參數發生錯誤，狀態碼: {}", statusCode);
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"發送消息失敗，狀態碼: " + statusCode + "\"}")
                        .build();
            }
		} catch (Exception e) {
			logger.error("發送LINE通知過程中發生錯誤:",e.getMessage(),e);
		}
		return null;
		
		
		
	}

}
