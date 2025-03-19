package com.controller;

import java.util.Base64;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.model.email.EmailRequest;
import com.model.email.EmailResponse;
import com.model.email.ErrorResponse;
import com.service.EmailService3;



@Path("/api/v1")
public class EmailController {

    private static final Logger logger = LogManager.getLogger(EmailController.class);

    EmailService3 emailService = new EmailService3();

    @POST
    @Path("/send-email")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmail(EmailRequest request) {
        logger.info("收到發送郵件的請求。"); // 日誌：收到發送郵件請求
        

        // 檢查請求是否為空
        if (request == null) {
            logger.warn("請求體為空。"); // 日誌：請求體為空警告
            ErrorResponse error = new ErrorResponse("ERROR", "INVALID_REQUEST", "Request body cannot be null.");
            return Response.status(Response.Status.BAD_REQUEST)
            		.entity(error).build();
        }

        
        // 檢查收件人是否為空
        if (request.getRecipients() == null ) {
            logger.warn("收件人列表為空。"); // 日誌：收件人為空警告
            ErrorResponse error = new ErrorResponse("ERROR", "INVALID_RECIPIENTS", "Recipients cannot be null or empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
       

        // 檢查主題是否為空
        if (request.getSubject() == null || request.getSubject().isEmpty()) {
            logger.warn("郵件主題為空。"); // 日誌：主題為空警告
            ErrorResponse error = new ErrorResponse("ERROR", "INVALID_SUBJECT", "Subject cannot be null or empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // 檢查文本內容是否為空
        if (request.getTextBody() == null || request.getTextBody().isEmpty()) {
            logger.warn("文本內容為空。"); // 日誌：文本內容為空警告
            ErrorResponse error = new ErrorResponse("ERROR", "INVALID_TEXT_BODY", "Text body cannot be null or empty.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        try {
        	//執行業務邏輯
            EmailResponse response = emailService.sendEmail(request); // 調用 emailService 發送郵件
            logger.info("郵件成功發送給: {}", String.join(",", request.getRecipients())); // 郵件發送成功並開始記錄
            return Response.ok(response).build();
        } catch (Exception e) {
            logger.error("發送郵件失敗: {}", e.getMessage(), e); // 日誌：處理發送失敗
            ErrorResponse error = new ErrorResponse("ERROR", "INTERNAL_ERROR", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }
    
}
