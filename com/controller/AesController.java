package com.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.model.AES.AesRequest;
import com.model.AES.AesResponse;
import com.service.AesService;



@Path("/aes")
public class AesController {
    private static final Logger logger = LogManager.getLogger(AesController.class);

    @POST
    @Path("/encrypt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response encrypt(AesRequest request) {
        // 先進行檢測說裡面的數據是否有效
        if (request == null || request.getData() == null || request.getData().isEmpty()) {
            logger.info("加密裡面是空的or是無效的");
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Request data cannot be null or empty.\"}")
                .build();
        }

        try {
            // 生成密鑰和IV先存起來
            String key = AesService.generateKey();
            String iv = AesService.generateIV();
            String encryptedData = AesService.encrypt(request.getData(), key, iv);
            logger.info("加密數據: {}", encryptedData);
            return Response.ok(new AesResponse(encryptedData, key, iv)).build();
        } catch (Exception e) {
            logger.error("加密過程中發生錯誤: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"An unexpected error occurred: " + e.getMessage() + "\"}")
                .build();
        }
    }
    
    @POST
    @Path("/decrypt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    
    public Response decrypt(AesRequest request) {
        // 先進行檢測說裡面的數據是否有效
        if (request == null || request.getData() == null || request.getData().isEmpty()) {
            logger.info("解密裡面是空的or是無效的");
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"Request data cannot be null or empty.\"}")
                .build();
        }

        try {
        	String decryptedData = AesService.decryt(request.getData(), request.getKey(), request.getIv());
        	logger.info("解密成功，解密後的數據: {}",decryptedData);
        	return Response.ok(new AesResponse(decryptedData))
        			.build();
        	
        } catch (Exception e) {
        	logger.error("解密過程中發生錯誤: {}",e.getMessage(),e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\":\"非預期的錯誤發生: " + e.getMessage() + "\"}")
                .build();
        }
		
		
    }
}