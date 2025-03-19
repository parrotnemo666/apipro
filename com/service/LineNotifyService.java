package com.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dao.LineNotifyDAO;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LineNotifyService {

	public static void main(String[] args) {
		// 創建LineNotifyService實例
		LineNotifyService service = new LineNotifyService();

		// 設置測試參數
		String token = "Kx8mOcL0Vm8qFgXGFG15vTrHonBqtP7lMs5BK4WLkUF"; 
		String message = "main方法測試!";
		String proxyHost = "proxy2.xu06p.com.tw"; 
		int proxyPort = 80; // 替換為你的代理端口

		// 調用sendLineNotify方法
		int statusCode = service.sendLineNotify(token, message, proxyHost, proxyPort);

		// 輸出結果
		System.out.println("發送結果狀態碼: " + statusCode);
		if (statusCode == 200) {
			System.out.println("LINE通知發送成功！");
		} else {
			System.out.println("LINE通知發送失敗。請檢查日誌以獲取更多信息。");
		}
	}

	private static final Logger logger = LogManager.getLogger(LineNotifyService.class);
	private static final String LINE_NOTIFY_API_URL = "https://notify-api.line.me/api/notify";
//	private LineNotifyDAO lineNotifyDAO;
//
//	// 構造子初始化，和直接new功能依樣但是比較可以調整裡面的內容
//	public LineNotifyService() {
//		this.lineNotifyDAO = new LineNotifyDAO();
//	}

	public int sendLineNotify(String token, String message, String proxyHost, int proxyPort) {
		logger.info("準備發送 LINE 通知。Token: {}, Message: {}", token, message);

		HttpURLConnection connection = null;
		LineNotifyDAO lineNotifyDAO = new LineNotifyDAO();	
		try {
			// 創建信任所有證書的 SSL 上下文
			SSLContext sslContext = createTrustAllSSLContext();

			// 設置HTTP代理，用於網路連接
			// Proxy.Type.HTTP是一個HTTP代理
			// 新增並且指定一個伺服器的主機名和端口號(放到proxy裡面) new proxy(想要設定的proxy類型 ,代理服務器的地址)

			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

			// 創建連接
			// 使用這個URL(LINE的地址)和代理()來打開網路連接

			URL url = new URL(LINE_NOTIFY_API_URL);
            //connection= (HttpURLConnection) url.openConnection(proxy);

			// url.openConnection(proxy)是利用到之前的proxy去開啟連接
			URLConnection urlConnection = url.openConnection(proxy);
			
			// 並類型轉換成為HttpURLConnection(可以比較方便訪問HTTP特定的方法)
			connection = (HttpURLConnection) urlConnection;

			// 檢查是否為HTTPS的連接
			boolean isHttps = isHttpsConnection(connection);
			

			if (isHttps) {
				// ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
				// 1. 先強制轉型成為HttpsURLConnection(因為要去使用setSSLSocketFactory方法)
				
				HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;

				
				// 2. 獲取SSLSOCKET工廠 SSL context是之前創造的對象
				SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

				// 3. 為HTTPS設置SSLSOCKET工廠，讓他使用我的SSL設置
				httpsURLConnection.setSSLSocketFactory(sslSocketFactory);

				logger.info("已經把HTTPS設自為 自定義的SSL Socket工廠");
			} else {
				logger.info("這是一個普通的HTTP請求");
			}

			// 設置請求header
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("Authorization", "Bearer " + token);
			connection.setDoOutput(true); // 表示準備發出訊息給服務器

			// 發送請求體
			String postData = "message=" + message;
			logger.info("準備發送的數據:{}", postData);

			try (OutputStream outputStream = connection.getOutputStream()) {
//				outputStream.write(postData.getBytes());//將STRING轉換成為BYTE並寫入
				// 將string轉換成為byte
				byte[] postDataBytes = postData.getBytes("UTF-8");
				logger.info("數據已經轉換成為byte");
				// 將byte寫入輸出流裡面
				outputStream.write(postDataBytes);
				logger.info("數據已經寫入輸出流");
				// 刷新輸出流，並且確保所有數據都有傳送
//				outputStream.flush();
				logger.info("數據已經傳入輸出流完成，並完成傳送 message:{}",message);
			}

			// 獲取響應
			int statusCode = connection.getResponseCode();
			String responseBody = readResponseBody(connection);
			logger.info("LINE 通知請求已發送。響應狀態碼: {}", statusCode);

//			// 讀取響應體
//			String response = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);


			logger.info("響應內容: {}", responseBody);
			
			// 記錄通知到數據庫

			LineNotifyDAO.logToDatabase(token, message, statusCode,responseBody);
			return statusCode;

		} catch (Exception e) {
			logger.error("發送 LINE 通知時發生錯誤: {}", e.getMessage(), e);
			return -1;
			
			//發生任何錯誤都會出現這個
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	// 簡單去判斷說是否為https的連接
	private boolean isHttpsConnection(HttpURLConnection connection) {
//		return "https".equalsIgnoreCase(connection.getURL().getProtocol());
		//從connection 裡面找到連接裡面的訊息(協議、主機名、端口都在裡面
		URL url = connection.getURL();
		
		String protool = url.getProtocol();
		//去看裡面有沒有符合HTTPS一樣
		return "https".equalsIgnoreCase(protool);
		
	}

//建立一個信任所有證書的上下文SSL憑證
	private SSLContext createTrustAllSSLContext() throws Exception {
		// 建立一個數組用於放所有SSL憑證
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			// 代表接受所有的發行者
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			// 不進行客戶端的驗證
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			// 不進行服務器端的驗證
			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };
        //創建SSL上下文並初始化
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustAllCerts, new SecureRandom());
		return sslContext;
	}
	
	 // 讀取HTTP響應體
    private String readResponseBody(HttpURLConnection connection) throws Exception {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
}