package com.example.v2.controller;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.v2.model.in1.EncryptionRequest;
import com.example.v2.model.in1.EncryptionResponse;
import com.example.v2.model.in1.ErrorResponse;
import com.example.v2.service.in1.AESEncryptionService;
import com.example.v2.service.in1.HashEncryptionService;
import com.example.v2.service.in1.RSAEncryptionService;

/**
 * 加密服務控制器 提供統一的三合一加密服務接口，支援AES、RSA和Hash三種加密方式
 */
@Path("/threeinone")
public class EncryptionController {

	// 注入加密服務
	private final AESEncryptionService aesService;
	private final RSAEncryptionService rsaService;
	private final HashEncryptionService hashService;

	// 日誌記錄器
	private static final Logger logger = LogManager.getLogger(EncryptionController.class);

	/**
	 * 構造函數 初始化各個加密服務
	 */
	public EncryptionController() {
		this.aesService = new AESEncryptionService();
		this.rsaService = new RSAEncryptionService();
		this.hashService = new HashEncryptionService();
	}

	/**
	 * 統一的加密/解密處理端點 根據傳入的type參數選擇對應的加密方式進行處理
	 * 
	 * @param request 加密請求對象，包含加密類型、操作類型、數據和參數
	 * @return Response 包含處理結果的響應對象
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response process(EncryptionRequest request) {
		// 記錄接收到的請求
		logger.info("接收到加密請求 - 類型: {}, 操作: {}", request.getType(), request.getOperation());

		try {
			// 基本參數驗證
			validateRequest(request);

			// 根據加密類型選擇對應的處理方法
			switch (request.getType().toUpperCase()) {
			case "AES":
				return handleAESRequest(request);
			case "RSA":
				return handleRSARequest(request);
			case "HASH":
				return handleHashRequest(request);
			default:
				logger.warn("不支援的加密類型: {}", request.getType());
				return Response.status(Response.Status.BAD_REQUEST)
						.entity(new ErrorResponse("error", "不支援的加密類型: " + request.getType())).build();
			}

		} catch (IllegalArgumentException e) {
			// 參數驗證失敗
			logger.warn("請求參數驗證失敗: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("error", "參數驗證失敗: " + e.getMessage())).build();

		} catch (Exception e) {
			// 未預期的錯誤
			logger.error("處理請求時發生異常", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("error", "內部服務器錯誤: " + e.getMessage())).build();
		}
	}

	/**
	 * 基本請求參數驗證
	 * 
	 * @param request 加密請求對象
	 * @throws IllegalArgumentException 如果參數驗證失敗
	 */
	private void validateRequest(EncryptionRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("請求不能為空");
		}
		if (request.getType() == null || request.getType().trim().isEmpty()) {
			throw new IllegalArgumentException("加密類型不能為空");
		}
		if (request.getOperation() == null || request.getOperation().trim().isEmpty()) {
			throw new IllegalArgumentException("操作類型不能為空");
		}
		if (request.getData() == null || request.getData().trim().isEmpty()) {
			throw new IllegalArgumentException("待處理數據不能為空");
		}
		if (request.getParams() == null) {
			throw new IllegalArgumentException("加密參數不能為空");
		}
	}

	/**
	 * 處理AES加密/解密請求
	 * 
	 * @param request AES加密請求
	 * @return Response 處理結果
	 */
	private Response handleAESRequest(EncryptionRequest request) {
		try {
			// AES特定參數驗證
			validateAESParams(request);

			// 執行AES加密/解密
			EncryptionResponse response = aesService.processRequest(request);

			// 記錄處理結果
			logger.info("AES處理完成 - 操作: {}, 狀態: {}", request.getOperation(), response.getStatus());

			return Response.ok(response).build();

		} catch (IllegalArgumentException e) {
			logger.warn("AES參數驗證失敗: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("error", "AES參數錯誤: " + e.getMessage())).build();

		} catch (Exception e) {
			logger.error("AES處理失敗", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("error", "AES處理失敗: " + e.getMessage())).build();
		}
	}

	/**
	 * 處理RSA加密/解密請求
	 * 
	 * @param request RSA加密請求
	 * @return Response 處理結果
	 */
	private Response handleRSARequest(EncryptionRequest request) {
		try {
			// RSA特定參數驗證
			validateRSAParams(request);

			// 執行RSA加密/解密
			EncryptionResponse response = rsaService.processRequest(request);

			// 記錄處理結果
			logger.info("RSA處理完成 - 操作: {}, 狀態: {}", request.getOperation(), response.getStatus());

			return Response.ok(response).build();

		} catch (IllegalArgumentException e) {
			logger.warn("RSA參數驗證失敗: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("error", "RSA參數錯誤: " + e.getMessage())).build();

		} catch (Exception e) {
			logger.error("RSA處理失敗", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("error", "RSA處理失敗: " + e.getMessage())).build();
		}
	}

	/**
	 * 處理Hash計算請求
	 * 
	 * @param request Hash計算請求
	 * @return Response 處理結果
	 */
	private Response handleHashRequest(EncryptionRequest request) {
		try {
			// Hash特定參數驗證
			validateHashParams(request);

			// 執行Hash計算
			EncryptionResponse response = hashService.processRequest(request);

			// 記錄處理結果
			logger.info("Hash處理完成 - 算法: {}, 狀態: {}", request.getParams().getOrDefault("algorithm", "SHA-256"),
					response.getStatus());

			return Response.ok(response).build();

		} catch (IllegalArgumentException e) {
			logger.warn("Hash參數驗證失敗: {}", e.getMessage());
			return Response.status(Response.Status.BAD_REQUEST)
					.entity(new ErrorResponse("error", "Hash參數錯誤: " + e.getMessage())).build();

		} catch (Exception e) {
			logger.error("Hash處理失敗", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("error", "Hash處理失敗: " + e.getMessage())).build();
		}
	}

	/**
	 * 驗證AES加密所需的參數
	 * 
	 * @param request 加密請求
	 * @throws IllegalArgumentException 如果參數驗證失敗
	 */
	private void validateAESParams(EncryptionRequest request) {
		Map<String, String> params = request.getParams();
		if (!params.containsKey("key") || params.get("key").trim().isEmpty()) {
			throw new IllegalArgumentException("AES密鑰不能為空");
		}
		if (!params.containsKey("iv") || params.get("iv").trim().isEmpty()) {
			throw new IllegalArgumentException("AES向量不能為空");
		}
		if (!("encrypt".equalsIgnoreCase(request.getOperation())
				|| "decrypt".equalsIgnoreCase(request.getOperation()))) {
			throw new IllegalArgumentException("不支援的AES操作類型: " + request.getOperation());
		}
	}

	/**
	 * 驗證RSA加密所需的參數
	 * 
	 * @param request 加密請求
	 * @throws IllegalArgumentException 如果參數驗證失敗
	 */
	private void validateRSAParams(EncryptionRequest request) {
		Map<String, String> params = request.getParams();
		if ("encrypt".equalsIgnoreCase(request.getOperation())) {
			if (!params.containsKey("publicKey") || params.get("publicKey").trim().isEmpty()) {
				throw new IllegalArgumentException("RSA公鑰不能為空");
			}
		} else if ("decrypt".equalsIgnoreCase(request.getOperation())) {
			if (!params.containsKey("privateKey") || params.get("privateKey").trim().isEmpty()) {
				throw new IllegalArgumentException("RSA私鑰不能為空");
			}
		} else {
			throw new IllegalArgumentException("不支援的RSA操作類型: " + request.getOperation());
		}
	}

	/**
	 * 驗證Hash計算所需的參數
	 * 
	 * @param request 加密請求
	 * @throws IllegalArgumentException 如果參數驗證失敗
	 */
	private void validateHashParams(EncryptionRequest request) {
		if (!"encrypt".equalsIgnoreCase(request.getOperation())) {
			throw new IllegalArgumentException("Hash只支持加密操作");
		}

		Map<String, String> params = request.getParams();
		if (params.containsKey("algorithm")) {
			String algorithm = params.get("algorithm");
			if (!hashService.getSupportedAlgorithms().contains(algorithm)) {
				throw new IllegalArgumentException("不支援的Hash算法: " + algorithm);
			}
		}
	}

	/**
	 * 生成AES密鑰和IV
	 */
	@GET
	@Path("/aes/generate-key")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateAESKey() {
		try {
			logger.info("生成新的AES密鑰和IV");
			Map<String, String> keyAndIV = aesService.generateKeyAndIV();
			return Response.ok(keyAndIV).build();
		} catch (Exception e) {
			logger.error("生成AES密鑰失敗", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("error", e.getMessage())).build();
		}
	}

	/**
	 * 生成RSA密鑰對
	 */
	@GET
	@Path("/rsa/generate-keypair")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateRSAKeyPair() {
		try {
			logger.info("生成新的RSA密鑰對");
			Map<String, String> keyPair = rsaService.generateKeyPair();
			return Response.ok(keyPair).build();
		} catch (Exception e) {
			logger.error("生成RSA密鑰對失敗", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("error", e.getMessage())).build();
		}
	}

	/**
	 * 獲取支援的Hash算法列表
	 */
	@GET
	@Path("/hash/algorithms")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSupportedHashAlgorithms() {
		try {
			logger.info("獲取支援的Hash算法列表");
			List<String> algorithms = hashService.getSupportedAlgorithms();
			return Response.ok(algorithms).build();
		} catch (Exception e) {
			logger.error("獲取Hash算法列表失敗", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(new ErrorResponse("error", e.getMessage())).build();
		}
	}
}