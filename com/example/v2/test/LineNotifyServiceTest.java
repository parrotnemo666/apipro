
package com.example.v2.test;

import com.example.v2.model.LineNotifyErrorResponse;
import com.example.v2.model.LineNotifyResponse;
import com.example.v2.service.LineNotifyService;

public class LineNotifyServiceTest {

    private static final String VALID_TOKEN = "Kx8mOcL0Vm8qFgXGFG15vTrHonBqtP7lMs5BK4WLkUf";
    private static final String PROXY_HOST = "proxy2.xu06p.com.tw";
    private static final int PROXY_PORT = 80;
    
    private static LineNotifyService service = new LineNotifyService();

    public static void main(String[] args) {
        // 執行所有測試
        testNormalCase();
//        testMessageTooLong();
//        testInvalidToken();
//        testSSLException();
//        testSocketException();
//        testIOException();
    }

    private static void testNormalCase() {
        runTest("正常情況測試", VALID_TOKEN, "這是一條來自 linenotifyV2 的測試消息", PROXY_HOST, PROXY_PORT);
    }

    private static void testMessageTooLong() {
        String longMessage = "linenotifyV2訊息發送 " + "a".repeat(200); // 確保總長度超過200字符
        runTest("消息過長測試", VALID_TOKEN, longMessage, PROXY_HOST, PROXY_PORT);
    }

    private static void testInvalidToken() {
        runTest("無效Token測試", "無效的Token", "linenotifyV2訊息發送測試", PROXY_HOST, PROXY_PORT);
    }

    private static void testSSLException() {
        runTest("SSL異常測試", VALID_TOKEN, "linenotifyV2訊息發送測試", "THROW_SSL_EXCEPTION", PROXY_PORT);
    }

    private static void testSocketException() {
        runTest("Socket異常測試", VALID_TOKEN, "linenotifyV2訊息發送測試", "THROW_SOCKET_EXCEPTION", PROXY_PORT);
    }

    private static void testIOException() {
        runTest("IO異常測試", VALID_TOKEN, "linenotifyV2訊息發送測試", "THROW_IO_EXCEPTION", PROXY_PORT);
    }

    
    //可以用來跑的各種exception方法
    private static void runTest(String testName, String token, String message, String proxyHost, int proxyPort) {
        System.out.println("執行測試: " + testName);
        try {
            Object result = service.sendLineNotify(token, message);
            if (result instanceof LineNotifyResponse) {
                LineNotifyResponse response = (LineNotifyResponse) result;
                System.out.println("測試結果: 成功 - {}" + response.getMessage());
                
            } else if (result instanceof LineNotifyErrorResponse) {
                LineNotifyErrorResponse error = (LineNotifyErrorResponse) result;
                System.out.println("測試結果: 錯誤 - 代碼: " + error.getErrorCode() + ", 消息: " + error.getErrorMessage());
            
            } else {
                System.out.println("測試結果: 未知響應類型");
            }
        } catch (Exception e) {
            System.out.println("測試結果: 捕獲到異常 - " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("--------------------");
    }
}
