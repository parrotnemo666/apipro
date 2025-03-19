package com.example.v2.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.example.v2.dao.LineNotifyDAO;
import com.example.v2.dao.LineNotifyDAO2;
import com.example.v2.model.LineNotifyErrorResponse;
import com.example.v2.model.LineNotifyResponse;
import com.other.EmailConfig;

import javax.net.ssl.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

//=================================== 
//目的:實現serviece裡面的核心業務邏輯
//1. 符合單一職責原則:專注處理LN的業務邏輯
//2. 輸入內容進行驗證:使用者內容會進行初步的篩選，如果字數超過限制也直接拒絕
//3. 錯誤處理:try catch  + if條件判斷 全面捕獲操作過程 +自訂義的錯誤代碼回復，可以快速找到問題並回復
//4. 安全性:忽略SSL認證和處理HTTPS連接
//===================================

public class LineNotifyService {
	private static final Logger logger = LogManager.getLogger(LineNotifyService.class);
	private static final String LINE_NOTIFY_API_URL = "https://notify-api.line.me/api/notify";
	private static final int MAX_MESSAGE_LENGTH = 200;
	
	// 設置代理服務，方便從公司電腦發送出去，先寫的原因是可以讓properties讀不到時，可以有個備案
	private static final String proxyHost = "proxy2.xu06p.com.tw";
	private static final int proxyPort = 80;
	

//	private LineNotifyDAO lineNotifyDAO;
//
//	public LineNotifyService() {
//		this.lineNotifyDAO = new LineNotifyDAO();
//	}

	
	LineNotifyDAO2 lineNotifyDAO2 = new  LineNotifyDAO2();
//	  =================================== 
//	  開始往linenotify進行打API
//    1. 先進行SSL的處理後
//	  2. 設置HTTPS設置和代理服務
//	  3. 向line server發送請求
//	  4. line server回傳狀態碼並解析狀態碼
// 	  5. 進行DAO紀錄相關結果
//	  6. 建立LNRESPONSE，回應給controller
//	  ===================================
	public Object sendLineNotify(String token, String message) {
		// 定義後再去properties把參數挖出來，雙重保險
		final int proxyPort = Integer.parseInt(EmailConfig.getProperty("proxyPort"));
		final String proxyHost = EmailConfig.getProperty("proxyHost");

		logger.info("準備發送 LINE 通知。Token: {}, Message: {}", token, message);

		// 字數超過限制會回退
		if (message.length() > MAX_MESSAGE_LENGTH) {
			logger.warn("消息長度超過限制: {}", message.length());
			return new LineNotifyErrorResponse("LN004", "MESSAGE_TOO_LONG", "消息長度超過" + MAX_MESSAGE_LENGTH + "個字符");
		}

		HttpURLConnection connection = null;
		try {
			// 創建信任所有證書的 SSL 上下文
			SSLContext sslContext = createTrustAllSSLContext();

			// 手動添加exception去進行測試
//			if (true) {
////				throw new SocketException();
////				throw new IOException();
//			}

			// 設置HTTP代理，用於網路連接，Proxy.Type.HTTP是一個HTTP代理
			// 新增並且指定一個伺服器的主機名和端口號(放到proxy裡面) new proxy(想要設定的proxy類型 ,代理服務器的地址)

			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));

			// 創建連接
			// 使用這個URL(LINE的地址)和代理()來打開網路連接
			URL url = new URL(LINE_NOTIFY_API_URL);

			// url.openConnection(proxy)是利用到之前的proxy去開啟連接
			URLConnection urlConnection = url.openConnection(proxy);

			// 並類型轉換成為HttpURLConnection(可以比較方便訪問HTTP特定的方法)
			connection = (HttpURLConnection) urlConnection;


			// 1. 先強制轉型成為HttpsURLConnection(因為要去使用setSSLSocketFactory方法)
			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) connection;

			// 2. 獲取SSLSOCKET工廠 SSL context是之前創造的對象
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

			// 3. 為HTTPS設置SSLSOCKET工廠，讓他使用我的SSL設置
			httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
			logger.info("已經把HTTPS設自為 自定義的SSL Socket工廠");


			// 處理請求屬性
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Authorization", "Bearer " + token);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setDoOutput(true);

			// 發送請求
			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = ("message=" + URLEncoder.encode(message, "UTF-8")).getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}
			
			


			// 處理回應，紀錄狀態碼和body(自己手寫readResponseBody去讀body)
			int statusCode = connection.getResponseCode();
			
//			String responseBody = readResponseBody(connection);
			
//			logger.info("LINE 通知請求已發送。響應狀態碼: {}", statusCode);

            String responseBody = "";
			if (statusCode >= 200 && statusCode < 300) {
				responseBody = readInputStream(connection.getInputStream());
			} else {
				responseBody = readInputStream(connection.getErrorStream());
			}
			logger.info("LINE 通知請求已發送。響應狀態碼: {}, 響應體: {}", statusCode, responseBody);
//			  =================================== 
//			  開始往linenotify進行打API
//		      1. 開始把紀錄寫入自己的DB2裡面紀錄 
//			  ===================================
			logger.info("準備開始記錄到資料庫裏面");
			LineNotifyErrorResponse dbError = lineNotifyDAO2.logToDatabase(token, message, statusCode, responseBody);
			if (dbError != null) {
				// 紀錄具體發生錯誤到log裡面
				logger.error("數據庫記錄失敗: {}", dbError.getErrorMessage());
				return dbError;
			}

//			int statusCode1 = statusCode;
//			String responseBody1  = responseBody.toString(); //
			// ===================================
			// 目的:開始進行條件判斷
			// 如果今天line的server狀態碼回多少代表什麼意思，用if再回覆給controller
			// 如果今天在trycatch裡面發生錯誤的話，會有那些預料中的exception，那我要做相應的處理
			// ===================================
			if (statusCode >= 200 && statusCode < 300) {
				return new LineNotifyResponse("SUCCESS", "通知發送成功");
//				return new LineNotifyResponse(statusCode1, responseBody1);
				
				
				
			} else if (statusCode == 401) {
				return new LineNotifyErrorResponse("LN201", "UNAUTHORIZED", "無效的token");
			} else if (statusCode == 500) {
				return new LineNotifyErrorResponse("LN301", "SERVER_ERROR", "LINE Notify 服務器內部發生錯誤");
			} else {
				return new LineNotifyErrorResponse("LN402", "UNKNOWN_RESPONSE", "未知的響應狀態碼: " + statusCode);
			}

		} catch (SocketException e) {
			logger.error("代理服務器連接失敗", e);
			return new LineNotifyErrorResponse("LN102", "PROXY_CONNECTION_FAILED", "代理服務器連接失敗: " + e.getMessage());
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			logger.error("SSL 連接錯誤", e);
			return new LineNotifyErrorResponse("LN101", "SSL_ERROR", "SSL 連接錯誤: " + e.getMessage());
		} catch (IOException e) {
			logger.error("HTTPS網絡連接錯誤", e);
			return new LineNotifyErrorResponse("LN103", "NETWORK_ERROR", "網絡連接錯誤: " + e.getMessage());
		} catch (Exception e) {
			logger.error("發送 LINE 通知時發生未知錯誤", e);
			return new LineNotifyErrorResponse("LN901", "UNKNOWN_ERROR", "發送 LINE 通知時發生未知錯誤: " + e.getMessage());
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}



	// 建立一個信任所有證書的上下文SSL憑證
	private SSLContext createTrustAllSSLContext() throws KeyManagementException, NoSuchAlgorithmException {
//手動拋出SSL錯誤
//		if (true) {
//			throw new SSLException("這邊是SSL的說明string");
//		}
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
		// 創建SSL上下文並初始化
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustAllCerts, new SecureRandom());

		return sslContext;
	}
//讀取裡面的東西
	private String readInputStream(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			return "";
		}

		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
		}
		return response.toString();
	}

//	// 簡單去判斷說是否為https的連接
//	private boolean isHttpsConnection(HttpURLConnection connection) {
////			return "https".equalsIgnoreCase(connection.getURL().getProtocol());
//		// 從connection 裡面找到連接裡面的訊息(協議、主機名、端口都在裡面
//		URL url = connection.getURL();
//
//		String protool = url.getProtocol();
//
//		// 去看裡面有沒有符合HTTPS一樣
//		return "https".equalsIgnoreCase(protool);
//
//	}
}
