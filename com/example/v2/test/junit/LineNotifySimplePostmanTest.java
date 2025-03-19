package com.example.v2.test.junit;


import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class LineNotifySimplePostmanTest {
    private static final Logger logger = LogManager.getLogger(LineNotifySimplePostmanTest.class);
    
    // 你的API端點
    private static final String API_URL = "http://localhost:8087/apipro/api/linenotifyV2";
    private static final String TOKEN = "Kx8mOcL0Vm8qFgXGFG15vTrHonBqtP7lMs5BK4WLkUf";
    private static final String MESSAGE = "肚子有點餓99";
    private WebTarget target;
    
    @Before
    public void setup() {
        Client client = ClientBuilder.newClient();
        target = client.target(API_URL);
        logger.info("測試環境初始化完成");
    }
    
    @Test
    public void test發送訊息() {
        logger.info("開始測試發送訊息");
        
        // 準備請求內容
        String requestBody = "{"
            + "\"token\": \"" + TOKEN + "\","
            + "\"message\": \""+ MESSAGE + "\""
            + "}";
            
        try {
            // 發送POST請求
            Response response = target
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(requestBody));
            
            // 輸出結果
            logger.info("回應狀態碼: " + response.getStatus());
            logger.info("回應內容: " + response.readEntity(String.class));
            
            // 驗證結果
            assertEquals(200, response.getStatus());
            
        } catch (Exception e) {
            logger.error("測試失敗", e);
            fail(e.getMessage());
        }
    }
}
