package com.other;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailConfig {
    private static Properties properties = new Properties();

    static {
        try {
        	//使用getResourceAsStream找到配置文件
        	InputStream input = EmailConfig.class.getResourceAsStream("/com/other/db.properties");
            // 使用絕對路徑加載配置文件
//            String configFilePath = "D:\\Test\\apipro\\src\\main\\java\\com\\other\\db.properties";
//            InputStream input = new FileInputStream(configFilePath);
//            String configFilePath = "com/other/db.properties";  //暫時相對位置不能用

            properties.load(input);
            
            System.out.println("Config is successfully loaded"
            		+ "!");
            
            //把裡面的內容(key, value) 儲存和列印
//            properties.forEach((key, value) -> 
//            System.out.println("<key>:"+ key + "       "+"<value>" + value));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
//公共的靜態方法，可以拿到key值後進properties裡面找
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
 // 添加 main 方法進行簡單測試
    public static void main(String[] args) {
        System.out.println("Running Config test...");

        // 測試讀取關鍵配置項
        testProperty("smtp.server");
        testProperty("sender.email");
        testProperty("sender.password");

       

        System.out.println("Config test completed.");
    }

    private static void testProperty(String key) {
        String value = getProperty(key);
        if (value != null) {
            System.out.println(key + " = " + value);
        } else {
            System.out.println(key + " is not found in the configuration.");
        }
    }
}
