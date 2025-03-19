package com.moneyTransfer.dao;



import java.sql.*;

import com.moneyTransfer.exception.DataAccessException;
import com.moneyTransfer.model.ExchangeTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易資料訪問層
 * 負責所有與資料庫相關的操作，包括交易記錄的查詢、保存和驗證
 */
public class TransactionDAO {
    private Connection connection;
    
    public TransactionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 在 Exchange Table 中查找交易記錄
     * @param transactionId 交易ID
     * @return 如果找到則返回交易記錄，否則返回null
     * @throws DataAccessException 當資料庫操作失敗時拋出
     */
    public ExchangeTransaction findExchangeTransaction(String transactionId) {
        String sql = "SELECT * FROM exchange_transactions WHERE transaction_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapToExchangeTransaction(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("查詢交易記錄失敗", e);
        }
        return null;
    }

    /**
     * 保存交易記錄到 History Table
     * @param transaction 要保存的交易記錄
     * @throws DataAccessException 當資料庫操作失敗時拋出
     */
    public void saveToHistory(ExchangeTransaction transaction) {
        String sql = """
            INSERT INTO transaction_history 
            (transaction_id, name, id_number, birth_date, nationality,
            resident_permit_no, permit_expiry_date, phone_number,
            currency, amount, transaction_type, update_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
            
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getName());
            pstmt.setString(3, transaction.getIdNumber());
            pstmt.setString(4, transaction.getBirthDate());
            pstmt.setString(5, transaction.getNationality());
            pstmt.setString(6, transaction.getResidentPermitNo());
            pstmt.setString(7, transaction.getPermitExpiryDate());
            pstmt.setString(8, transaction.getPhoneNumber());
            pstmt.setString(9, transaction.getCurrency());
            pstmt.setDouble(10, transaction.getAmount());
            pstmt.setString(11, transaction.getTransactionType());
            pstmt.setTimestamp(12, new Timestamp(transaction.getUpdateTime().getTime()));
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("保存歷史記錄失敗", e);
        }
    }

    /**
     * 保存失敗的交易記錄
     * @param transactionId 交易ID
     * @param reason 失敗原因
     * @throws DataAccessException 當資料庫操作失敗時拋出
     */
    public void saveToFailedTransactions(String transactionId, String reason) {
        String sql = """
            INSERT INTO failed_transactions 
            (transaction_id, failure_reason, failure_time, status)
            VALUES (?, ?, ?, ?)
            """;
            
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            pstmt.setString(2, reason);
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setString(4, "FAILED");
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("保存失敗記錄失敗", e);
        }
    }

    /**
     * 檢查交易是否已存在於歷史記錄中
     * @param transactionId 交易ID
     * @return 如果存在返回true，否則返回false
     * @throws DataAccessException 當資料庫操作失敗時拋出
     */
    public boolean existsInHistory(String transactionId) {
        String sql = "SELECT COUNT(*) FROM transaction_history WHERE transaction_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transactionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("檢查歷史記錄失敗", e);
        }
    }

    /**
     * 檢查相似交易
     * 查找30分鐘內相同金額和ID的交易
     * @param transaction 要檢查的交易
     * @return 如果存在相似交易返回true，否則返回false
     * @throws DataAccessException 當資料庫操作失敗時拋出
     */
    public boolean checkSimilarTransaction(ExchangeTransaction transaction) {
        String sql = """
            SELECT COUNT(*) FROM transaction_history 
            WHERE id_number = ? 
            AND amount = ? 
            AND currency = ?
            AND ABS(TIMESTAMPDIFF(MINUTE, update_time, ?)) <= 30
            """;
            
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getIdNumber());
            pstmt.setDouble(2, transaction.getAmount());
            pstmt.setString(3, transaction.getCurrency());
            pstmt.setTimestamp(4, new Timestamp(transaction.getUpdateTime().getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("檢查相似交易失敗", e);
        }
    }

    /**
     * 將 ResultSet 映射到 ExchangeTransaction 對象
     * @param rs ResultSet 結果集
     * @return 映射後的 ExchangeTransaction 對象
     * @throws SQLException 當資料讀取失敗時拋出
     */
    private ExchangeTransaction mapToExchangeTransaction(ResultSet rs) throws SQLException {
        ExchangeTransaction transaction = new ExchangeTransaction();
        transaction.setTransactionId(rs.getString("transaction_id"));
        transaction.setName(rs.getString("name"));
        transaction.setIdNumber(rs.getString("id_number"));
        transaction.setBirthDate(rs.getString("birth_date"));
        transaction.setNationality(rs.getString("nationality"));
        transaction.setResidentPermitNo(rs.getString("resident_permit_no"));
        transaction.setPermitExpiryDate(rs.getString("permit_expiry_date"));
        transaction.setPhoneNumber(rs.getString("phone_number"));
        transaction.setCurrency(rs.getString("currency"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setUpdateTime(rs.getTimestamp("update_time"));
        return transaction;
    }
}