package com.moneyTransfer.service;

import java.util.logging.Logger;

import com.moneyTransfer.dao.TransactionDAO;
import com.moneyTransfer.exception.DataAccessException;
import com.moneyTransfer.exception.ValidationException;
import com.moneyTransfer.model.ExchangeTransaction;
import com.moneyTransfer.model.TransactionResult;

import java.util.logging.Level;

/**
 * 交易服務層
 * 負責處理業務邏輯，協調各個組件的工作
 */
public class TransactionService {
    private static final Logger logger = Logger.getLogger(TransactionService.class.getName());
    private final TransactionDAO transactionDAO;

    public TransactionService(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    /**
     * 處理交易驗證
     * 包含查詢、驗證和保存過程
     * 
     * @param transactionId 交易ID
     * @return 交易處理結果
     */
    public TransactionResult processTransaction(String transactionId) {
        logger.info("開始處理交易: " + transactionId);
        TransactionResult result = new TransactionResult();
        
        try {
            // 步驟1: 查詢交易記錄
            ExchangeTransaction transaction = transactionDAO.findExchangeTransaction(transactionId);
            
            if (transaction != null) {
                logger.info("找到交易記錄: " + transactionId);
                
                // 步驟2: 檢查是否已存在於歷史記錄
                if (!transactionDAO.existsInHistory(transactionId)) {
                    // 步驟3: 檢查是否有相似交易
                    if (transactionDAO.checkSimilarTransaction(transaction)) {
                        logger.warning("發現相似交易: " + transactionId);
                        result.setSuccess(false);
                        result.setMessage("發現相似交易，請確認");
                        result.setErrorCode("W001");
                        return result;
                    }
                    
                    // 步驟4: 保存到歷史記錄
                    transactionDAO.saveToHistory(transaction);
                    logger.info("成功保存到歷史記錄: " + transactionId);
                    
                    result.setSuccess(true);
                    result.setMessage("交易記錄已成功保存");
                    result.setTransactionId(transactionId);
                } else {
                    logger.warning("交易記錄已存在於歷史資料庫: " + transactionId);
                    result.setSuccess(false);
                    result.setMessage("交易記錄已存在於歷史資料庫");
                    result.setErrorCode("E001");
                }
            } else {
                // 交易不存在，記錄到失敗表
                logger.warning("查無交易記錄: " + transactionId);
                transactionDAO.saveToFailedTransactions(transactionId, "查無交易記錄");
                
                result.setSuccess(false);
                result.setMessage("查無交易記錄");
                result.setErrorCode("E002");
            }
            
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "處理交易時發生資料存取錯誤", e);
            result.setSuccess(false);
            result.setMessage("處理交易時發生錯誤: " + e.getMessage());
            result.setErrorCode("E999");
            
            try {
                transactionDAO.saveToFailedTransactions(
                    transactionId, 
                    "資料庫操作失敗: " + e.getMessage()
                );
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "保存失敗記錄時發生錯誤", ex);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "處理交易時發生未預期的錯誤", e);
            result.setSuccess(false);
            result.setMessage("系統處理異常");
            result.setErrorCode("E999");
            
            try {
                transactionDAO.saveToFailedTransactions(
                    transactionId, 
                    "系統錯誤: " + e.getMessage()
                );
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "保存失敗記錄時發生錯誤", ex);
            }
        }
        
        return result;
    }

    /**
     * 驗證交易數據的有效性
     * 
     * @param transaction 要驗證的交易數據
     * @throws ValidationException 當驗證失敗時拋出
     */
    private void validateTransaction(ExchangeTransaction transaction) {
        if (transaction == null) {
            throw new ValidationException("交易數據不能為空");
        }
        if (transaction.getAmount() <= 0) {
            throw new ValidationException("交易金額必須大於0", "amount", "POSITIVE_AMOUNT");
        }
        if (transaction.getIdNumber() == null || transaction.getIdNumber().trim().isEmpty()) {
            throw new ValidationException("身份證號碼不能為空", "idNumber", "REQUIRED");
        }
        // 可以添加更多的驗證規則
    }

    /**
     * 根據交易ID獲取交易歷史記錄
     * 
     * @param transactionId 交易ID
     * @return 交易結果
     */
    public TransactionResult getTransactionHistory(String transactionId) {
        logger.info("查詢交易歷史: " + transactionId);
        TransactionResult result = new TransactionResult();
        
        try {
            ExchangeTransaction transaction = transactionDAO.findExchangeTransaction(transactionId);
            if (transaction != null) {
                result.setSuccess(true);
                result.setTransactionId(transactionId);
                // 可以設置更多的返回信息
            } else {
                result.setSuccess(false);
                result.setMessage("找不到交易記錄");
                result.setErrorCode("E004");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "查詢交易歷史時發生錯誤", e);
            result.setSuccess(false);
            result.setMessage("查詢失敗");
            result.setErrorCode("E999");
        }
        
        return result;
    }
}