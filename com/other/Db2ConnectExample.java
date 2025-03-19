package com.other;

import java.io.InputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;





public class Db2ConnectExample {
	
	public static void main(String[] args) {
		
		Properties properties = new Properties();
		Connection connection = null;
		
		try {
			InputStream input = Db2ConnectExample.class.getResourceAsStream("/com/other/db.properties");
			
			
		if (input == null) {
			throw new Exception("找不到配置文件的內容");
		}
		
		//加載properties文件
		properties.load(input);
		
		//從properties裡面抓出所需要的資料
		String jdbcurl =properties.getProperty("jdbc.url");
		String jdbcuesrname =properties.getProperty("jdbc.uesrname");
		String jdbcpassword =properties.getProperty("jdbc.password");
		
		//加載JDBC驅動
		Class.forName("com.ibm.db2.jcc.DB2Driver");
		
		//嘗試連接資料庫
		connection = DriverManager.getConnection(jdbcurl,jdbcuesrname,jdbcpassword);
		System.out.println("已經成功連接到資料庫");
		
		
		} catch (Exception e) {
			System.out.println("連接資料庫發生異常");
			e.printStackTrace();
		}
		
	}

}
