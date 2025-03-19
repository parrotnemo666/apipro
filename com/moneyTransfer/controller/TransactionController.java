package com.moneyTransfer.controller;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.moneyTransfer.exception.DataAccessException;
import com.moneyTransfer.exception.ValidationException;
import com.moneyTransfer.model.TransactionResult;
import com.moneyTransfer.service.TransactionService;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 交易驗證系統的 REST API 控制器
 * 處理所有與交易相關的 HTTP 請求
 */
@Path("/api/transactions")
public class TransactionController {
    private static final Logger logger = Logger.getLogger(TransactionController.class.getName());
    private final TransactionService transactionService;

    /**
     * 構造函數，注入所需的服務
     */
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 處理交易驗證請求
     * POST /api/transactions/verify
     * 
     * @param exchangeRecord 結匯交易資料
     * @param historyId 歷史交易ID
     * @return 交易處理結果
     */
    @POST
    @Path("/verify")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyTransaction(
            @QueryParam("transactionId") String transactionId) {
        
        logger.info("收到交易驗證請求: " + transactionId);
        
        try {
            // 基本參數驗證
            if (transactionId == null || transactionId.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(createErrorResponse("交易ID不能為空"))
                    .build();
            }

            // 調用服務層處理交易
            TransactionResult result = transactionService.processTransaction(transactionId);

            // 根據處理結果返回對應的響應
            if (result.isSuccess()) {
                logger.info("交易驗證成功: " + transactionId);
                return Response.ok(result).build();
            } else {
                logger.warning("交易驗證失敗: " + result.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
            }
            
        } catch (ValidationException e) {
            logger.log(Level.WARNING, "交易數據驗證失敗", e);
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(createErrorResponse("驗證失敗: " + e.getMessage()))
                .build();
                
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "交易處理時發生資料存取錯誤", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("資料庫操作異常: " + e.getMessage()))
                .build();
                
        } catch (Exception e) {
            logger.log(Level.SEVERE, "交易處理時發生未預期的錯誤", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("系統處理異常"))
                .build();
        }
    }

    /**
     * 查詢交易狀態
     * GET /api/transactions/{transactionId}
     */
    @GET
    @Path("/{transactionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTransactionStatus(
            @PathParam("transactionId") String transactionId) {
        
        logger.info("收到交易狀態查詢請求: " + transactionId);
        
        try {
            TransactionResult result = transactionService.getTransactionHistory(transactionId);
            
            if (result.isSuccess()) {
                return Response.ok(result).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(result)
                    .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "查詢交易狀態時發生錯誤", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("查詢處理異常"))
                .build();
        }
    }

    /**
     * 重新處理失敗的交易
     * POST /api/transactions/retry/{transactionId}
     */
    @POST
    @Path("/retry/{transactionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retryFailedTransaction(
            @PathParam("transactionId") String transactionId) {
        
        logger.info("收到重試失敗交易請求: " + transactionId);
        
        try {
            TransactionResult result = transactionService.processTransaction(transactionId);
            
            if (result.isSuccess()) {
                logger.info("重試交易成功: " + transactionId);
                return Response.ok(result).build();
            } else {
                logger.warning("重試交易失敗: " + result.getMessage());
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "重試交易時發生錯誤", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(createErrorResponse("重試處理異常"))
                .build();
        }
    }

    /**
     * 創建錯誤響應對象
     */
    private TransactionResult createErrorResponse(String message) {
        TransactionResult error = new TransactionResult();
        error.setSuccess(false);
        error.setMessage(message);
        error.setErrorCode("E999");
        return error;
    }

    /**
     * 全局異常處理器
     * 用於捕獲和處理未被其他地方捕獲的異常
     */
    @Provider
    public static class GlobalExceptionHandler implements ExceptionMapper<Throwable> {
        private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());
        
        @Override
        public Response toResponse(Throwable exception) {
            logger.log(Level.SEVERE, "未處理的異常", exception);
            
            TransactionResult error = new TransactionResult();
            error.setSuccess(false);
            error.setMessage("系統發生未預期的錯誤");
            error.setErrorCode("E999");
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }

    /**
     * 數據存取異常處理器
     */
    @Provider
    public static class DataAccessExceptionHandler implements ExceptionMapper<DataAccessException> {
        private static final Logger logger = Logger.getLogger(DataAccessExceptionHandler.class.getName());
        
        @Override
        public Response toResponse(DataAccessException exception) {
            logger.log(Level.SEVERE, "數據存取異常", exception);
            
            TransactionResult error = new TransactionResult();
            error.setSuccess(false);
            error.setMessage("數據庫操作失敗: " + exception.getMessage());
            error.setErrorCode("E001");
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
        }
    }

    /**
     * 驗證異常處理器
     */
//    @Provider
//    public static class ValidationExceptionHandler implements ExceptionMapper<ValidationException> {
//        private static final Logger logger = Logger.getLogger(ValidationExceptionHandler.class.getName());
//        
//        @Override
//        public Response toResponse(ValidationException exception) {
//            logger.log(Level.WARNING, "數據驗證異常", exception);
//            
//            TransactionResult error = new TransactionResult();
//            error.setSuccess(false);
//            error.setMessage("數據驗證失敗: " + exception.getMessage());
//            error.setErrorCode("E002");
//            
//            return Response.status(Response.Status.BAD_REQUEST)
//                .entity(error)
//                .type(MediaType.APPLICATION_JSON)
//                .build();
//        }
//    }
}