package com.example.v2.dao;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.other.Db2ConnectExample;



public class DBConnection {

//    private static final String URL = "jdbc:db2://172.16.46.181:25000/NEMODB1";
//    private static final String USER = "db2user";
//    private static final String PASSWORD = "ubot@12345";

	private static Properties properties;
	
	//會發生錯誤，會有 because "com.other.DBConnection.properties" is null 問題出現，跟
//	private static final String jdbcUrl = properties.getProperty("jdbc.url");
//	private static final String jdbcUesrname = properties.getProperty("jdbc.uesrname");
//	private static final String jdbcPassword = properties.getProperty("jdbc.password");

	static {
//		Properties properties = new Properties();
		try {
			loadProperties();

			// 載入DB2 JDBC驅動

			Class.forName("com.ibm.db2.jcc.DB2Driver");
		} catch (Exception e) {
			e.printStackTrace(); // 這裡可以改為使用Logger記錄，後面處理
			throw new RuntimeException("未找到DB2 JDBC驅動", e);
		}
	}
//加載數據庫的文件 從db.properties文件中加載
	public static void loadProperties()  {
		properties = new Properties();
		String resourcePath = "/com/other/db.properties";

		try {
			InputStream input = Db2ConnectExample.class.getResourceAsStream(resourcePath);
			if (input == null) {
//				throw new Exception("找不到配置文件的內容" + resourcePath);
				
			}
			properties.load(input);
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

//連接到數據DB2裡面的方法 獲取數據庫的連接
	public static Connection getConnection() {
		if (properties == null) {
//			throw new SQLException("數據配置未加載 (properties)");
			return null;
			//記log
		}
		Connection jdbcConnection;
		try {
			jdbcConnection = DriverManager.getConnection
					(properties.getProperty("jdbc.url"), 
				     properties.getProperty("jdbc.uesrname"),
					 properties.getProperty("jdbc.password"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//紀錄LOG起來
			return null;
		}
		
		// 創建並返回資料庫連接
		return jdbcConnection;
	}

//關閉數據庫連接的方法
	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				if (!connection.isClosed()) {
					connection.close();
					System.out.println("連接已關閉。");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("關閉資料庫連接時發生錯誤。");
			}
		}
	}
}
