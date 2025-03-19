package com.example.v2.controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.v2.model.AesRequest;
import com.example.v2.model.AesResponse;
import com.example.v2.service.AesService;

@Path("/aes/v2")
public class AesController {
	private static final Logger logger = LogManager.getLogger(AesController.class);
	private AesService aesService = new AesService();


	@POST
	@Path("/encrypt")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response encrypt(AesRequest request) {
		logger.info("收到加密請求");


		if (request == null || request.getData() == null || request.getData().isEmpty()) {
			logger.warn("加密請求數據為空或無效");
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new AesResponse("AES400", "請求數據不能為空")).build();
		}

		AesResponse response = aesService.encrypt(request.getData());

		if (response.getErrorCode() == null) {
			logger.info("加密成功");
			return Response.ok(response).build();//回data、KEY、IV
		} else {
			logger.error("加密失敗: {}", response.getErrorMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();//回errorCode、errorMessage
			//不要這樣寫的原因有保留原始信息進去
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new AesResponse("AES400", "請求數據不能為空")).build();
		}
	}


	@POST
	@Path("/decrypt")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response decrypt(AesRequest request) {
		logger.info("收到解密請求");

		if (request == null || request.getData() == null || 
			request.getData().isEmpty() || request.getKey() == null|| request.getIv() == null) {
			logger.warn("解密請求數據為空或無效");
			return Response.status(Response.Status.BAD_REQUEST)
			.entity(new AesResponse("AES400", "請求數據不完整")).build();
		}

		AesResponse response = aesService.decrypt(request.getData(), request.getKey(), request.getIv());

		if (response.getErrorCode() == null) {
			logger.info("解密成功");
			return Response.ok(response).build();
		} else {
			logger.error("解密失敗: {}", response.getErrorMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
		}
	}
	

}