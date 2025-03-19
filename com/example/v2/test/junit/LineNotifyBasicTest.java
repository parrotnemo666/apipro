package com.example.v2.test.junit;


import static org.junit.Assert.*;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;
import com.example.v2.controller.LineNotifyController;
import com.example.v2.model.LineNotifyRequest;
import com.example.v2.model.LineNotifyErrorResponse;

public class LineNotifyBasicTest {
    private static final Logger logger = LogManager.getLogger(LineNotifyBasicTest.class);
    private LineNotifyController controller = new LineNotifyController();
    
    @Test
    public void test空請求() {
        logger.info("開始測試: 空請求");
        
        // 發送空的請求
        Response response = controller.sendNotification(null);
        
        // 驗證結果
        assertEquals("空請求應該回傳400狀態碼", 400, response.getStatus());
        assertTrue("回應應該是LineNotifyErrorResponse類型", 
            response.getEntity() instanceof LineNotifyErrorResponse);
            
        LineNotifyErrorResponse error = (LineNotifyErrorResponse) response.getEntity();
        assertEquals("錯誤代碼應該是LN001", "LN001", error.getErrorCode());
        
      
        logger.info("空請求測試完成");
        logger.info("********************");
    }
    
    @Test
    public void test沒有Token() {
        logger.info("開始測試: 沒有Token的請求");
        
        // 創建請求但不設置token
        LineNotifyRequest request = new LineNotifyRequest();
        request.setMessage("測試訊息");
        
        Response response = controller.sendNotification(request);
        
        // 驗證結果
        assertEquals("沒有Token應該回傳400狀態碼", 400, response.getStatus());
        assertTrue("回應應該是LineNotifyErrorResponse類型", 
            response.getEntity() instanceof LineNotifyErrorResponse);
            
        LineNotifyErrorResponse error = (LineNotifyErrorResponse) response.getEntity();
        assertEquals("錯誤代碼應該是:LN002", "LN002", error.getErrorCode());
        assertEquals("錯誤回應應該是:Token 不能為空", "Token 不能為空", error.getErrorMessage());
        
        logger.info("沒有Token測試完成");
        logger.info("********************");
    }
    
    @Test
    public void test沒有訊息() {
        logger.info("開始測試: 沒有訊息的請求");
        
        // 創建請求，有token但沒有訊息
        LineNotifyRequest request = new LineNotifyRequest();
        request.setToken("test-token");
        
        Response response = controller.sendNotification(request);
        
        // 驗證結果
        assertEquals("沒有訊息應該回傳400狀態碼", 400, response.getStatus());
        assertTrue("回應應該是LineNotifyErrorResponse類型", 
            response.getEntity() instanceof LineNotifyErrorResponse);
            
        LineNotifyErrorResponse error = (LineNotifyErrorResponse) response.getEntity();
        assertEquals("錯誤代碼應該是LN003", "LN003", error.getErrorCode());
        assertEquals("錯誤回應應該是:消息內容不能為空", "消息內容不能為空", error.getErrorMessage());
        logger.info("沒有訊息測試完成");
        logger.info("********************");
    }
    
    
    
}