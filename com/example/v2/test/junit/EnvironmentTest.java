package com.example.v2.test.junit;
//創建一個簡單的測試類


import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvironmentTest {
 private static final Logger logger = LogManager.getLogger(EnvironmentTest.class);
 
 @Test
 public void testEnvironment() {
     logger.info("開始測試環境");
     assertTrue("測試環境正常", true);
     logger.info("環境測試完成");
 }
}