package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.other.DBConnection;

public class LineNotifyDAO {
    private static final Logger logger = LogManager.getLogger(LineNotifyDAO.class);
    // 存儲準備好的SQL語句，用於插入數據到LINE_NOTIFY表
    private static final String INSERT_SQL = 
        "INSERT INTO LINE_NOTIFY1(token, message, status_code, response_body, timestamp) VALUES (?, ?, ?, ?, ?)";
//這邊的static可能要確定不要有問題
    public static  void logToDatabase(String token, String message, int statusCode, String responseBody) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            
            // 設置PreparedStatement的參數
            statement.setString(1, token);
            statement.setString(2, message);
            statement.setInt(3, statusCode);
            statement.setString(4, responseBody);
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            
            System.out.println("完整的SQL語句:"+ INSERT_SQL);
            
            // 執行插入操作
            int rowsAffected = statement.executeUpdate();
            logger.info("API請求已經加入資料庫了，影響 {} 行", rowsAffected);
        } catch (SQLException e) {
            logger.error("保存API的錯誤已經存進資料庫中", e);
        }catch (Exception e) {
            logger.error("保存API的錯誤已經存進資料庫中", e);
        }
    }

    // 用於測試的主方法
    public static void main(String[] args) {
        // 測試資料
        String token = "Kx8mOcL0Vm8qFgXGFG15vTrHonBqtP7lMs5BK4WLkUf";
        String message = "這是一條DAO測試訊息";
        int statusCode = 200;
        String responseBody = "Success";

        // 調用logToDatabase方法
        logToDatabase(token, message, statusCode, responseBody);
        
        logger.info("測試資料已嘗試寫入資料庫。");
        logger.info("插入的資料：Token: {}, 訊息: {}, 狀態碼: {}, 響應體: {}", token, message, statusCode, responseBody);
    }
}

