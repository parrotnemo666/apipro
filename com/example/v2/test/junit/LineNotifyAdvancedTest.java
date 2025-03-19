package com.example.v2.test.junit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.core.Response;
import com.example.v2.controller.LineNotifyController;
import com.example.v2.model.LineNotifyRequest;
import com.example.v2.model.LineNotifyResponse;
import com.example.v2.model.LineNotifyErrorResponse;
import com.example.v2.service.LineNotifyService;

/**
 * LINE Notify進階功能測試 測試更多的業務邏輯和邊界情況
 */
public class LineNotifyAdvancedTest {
	private static final Logger logger = LogManager.getLogger(LineNotifyAdvancedTest.class);
    private static final String TOKEN = "Kx8mOcL0Vm8qFgXGFG15vTrHonBqtP7lMs5BK4WLkUf";

	@Mock
	private LineNotifyService lineNotifyService; // 模擬Service層

	private LineNotifyController controller;// 模擬Service層

	@Before
	public void setup() {
		// 初始化Mock
		MockitoAnnotations.openMocks(this);
		controller = new LineNotifyController();

		// 通過反射注入mock的service
		try {
			java.lang.reflect.Field serviceField = LineNotifyController.class.getDeclaredField("lineNotifyService");
			serviceField.setAccessible(true);
			serviceField.set(controller, lineNotifyService);
		} catch (Exception e) {
			logger.error("設置測試環境失敗", e);
		}
	}

	/**
	 * 測試正常發送訊息 預期：訊息正常發送，返回成功響應
	 */
	@Test
	public void test正常發送訊息() {
		logger.info("開始測試: 正常發送訊息");

		// 準備測試數據
		LineNotifyRequest request = new LineNotifyRequest();
		request.setToken(TOKEN);
		request.setMessage("Hello, this is a test message");

		// 模擬service層的行為
		when(lineNotifyService.sendLineNotify(anyString(), anyString()))
				.thenReturn(new LineNotifyResponse("SUCCESS", "訊息發送成功"));

		// 執行測試
		Response response = controller.sendNotification(request);

		// 驗證結果
		assertEquals("應該返回200狀態碼", 200, response.getStatus());
		assertTrue("應該返回LineNotifyResponse類型", response.getEntity() instanceof LineNotifyResponse);

		// 驗證service是否被正確調用
		verify(lineNotifyService, times(1)).sendLineNotify(eq(TOKEN), eq("Hello, this is a test message"));

		logger.info("正常發送訊息測試完成");
	}

	/**
	 * 測試訊息長度邊界值 預期：當訊息長度超過200字時應該返回錯誤
	 */
	@Test
	public void test訊息長度邊界值() {
		logger.info("開始測試: 訊息長度邊界值");

		// 創建200字的訊息（正好在限制內）
		StringBuilder validMessage = new StringBuilder();
		for (int i = 0; i < 200; i++) {
			validMessage.append("a");
		}

		// 創建201字的訊息（超過限制）
		StringBuilder invalidMessage = new StringBuilder(validMessage);
		invalidMessage.append("a");

		// 測試剛好200字的情況
		LineNotifyRequest validRequest = new LineNotifyRequest();
		validRequest.setToken(TOKEN);
		validRequest.setMessage(validMessage.toString());

		when(lineNotifyService.sendLineNotify(anyString(), eq(validMessage.toString())))
				.thenReturn(new LineNotifyResponse("SUCCESS", "訊息發送成功"));

		Response validResponse = controller.sendNotification(validRequest);
		assertEquals("200字應該可以正常發送", 200, validResponse.getStatus());

		// 測試201字的情況
		LineNotifyRequest invalidRequest = new LineNotifyRequest();
		invalidRequest.setToken(TOKEN);
		invalidRequest.setMessage(invalidMessage.toString());

		when(lineNotifyService.sendLineNotify(anyString(), eq(invalidMessage.toString())))
				.thenReturn(new LineNotifyErrorResponse("LN004", "MESSAGE_TOO_LONG", "訊息長度超過限制"));

		Response invalidResponse = controller.sendNotification(invalidRequest);
		assertEquals("201字應該返回錯誤", 500, invalidResponse.getStatus());
		
		logger.info("訊息長度邊界值測試完成");
	}

	/**
	 * 測試特殊字元處理 預期：特殊字元應該被正確處理
	 */
	@Test
	public void test特殊字元處理() {
		logger.info("開始測試: 特殊字元處理");

		// 準備包含特殊字元的訊息
		String specialMessage = "Special chars: !@#$%^&*()_+\n\t中文字";

		LineNotifyRequest request = new LineNotifyRequest();
		request.setToken(TOKEN);
		request.setMessage(specialMessage);

		when(lineNotifyService.sendLineNotify(anyString(), eq(specialMessage)))
				.thenReturn(new LineNotifyResponse("SUCCESS", "訊息發送成功"));

		Response response = controller.sendNotification(request);

		assertEquals("特殊字元應該可以正常發送", 200, response.getStatus());
		verify(lineNotifyService, times(1)).sendLineNotify(anyString(), eq(specialMessage));

		logger.info("特殊字元處理測試完成");
	}

	/**
	 * 測試Service層異常處理 預期：當Service層拋出異常時，應該正確處理並返回錯誤響應
	 */
	@Test
	public void testService層異常處理() {
		logger.info("開始測試: Service層異常處理");

		LineNotifyRequest request = new LineNotifyRequest();
		request.setToken(TOKEN);
		request.setMessage("test message");

		// 模擬service層拋出異常
		when(lineNotifyService.sendLineNotify(anyString(), anyString()))
				.thenReturn(new LineNotifyErrorResponse("LN501", "SERVER_ERROR", "伺服器內部錯誤"));

		Response response = controller.sendNotification(request);

		assertEquals("應該返回500狀態碼", 500, response.getStatus());
		assertTrue("應該返回LineNotifyErrorResponse", response.getEntity() instanceof LineNotifyErrorResponse);

		LineNotifyErrorResponse error = (LineNotifyErrorResponse) response.getEntity();
		assertEquals("應該返回正確的錯誤代碼", "LN501", error.getErrorCode());

		logger.info("Service層異常處理測試完成");
	}

	/**
	 * 測試重複發送 預期：短時間內重複發送應該被正確處理
	 */
	@Test
	public void test重複發送() {
		logger.info("開始測試: 重複發送處理");

		LineNotifyRequest request = new LineNotifyRequest();
		request.setToken(TOKEN);
		request.setMessage("test message");

		// 第一次發送
		when(lineNotifyService.sendLineNotify(anyString(), anyString()))
				.thenReturn(new LineNotifyResponse("SUCCESS", "訊息發送成功"));

		Response response1 = controller.sendNotification(request);
		assertEquals("第一次發送應該成功", 200, response1.getStatus());

		// 快速重複發送
		Response response2 = controller.sendNotification(request);
		assertEquals("重複發送應該也成功", 200, response2.getStatus());

		// 驗證service被調用了兩次
		verify(lineNotifyService, times(2)).sendLineNotify(anyString(), anyString());

		logger.info("重複發送處理測試完成");
	}
}