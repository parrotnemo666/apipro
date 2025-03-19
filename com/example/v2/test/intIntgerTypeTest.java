package com.example.v2.test;
public class intIntgerTypeTest {
    public static void main(String[] args) {
        // 1. 測試默認值差異
        testDefaultValues();
        
        // 2. 測試性能差異
        testPerformance();
        
        // 3. 測試Null處理
        testNullHandling();
        
        // 4. 測試裝箱拆箱
        testBoxingUnboxing();
        
        // 5. 測試方法使用
        testMethods();
    }
    
    // 測試默認值
    private static void testDefaultValues() {
        System.out.println("\n=== 測試默認值 ===");
        int primitiveInt = 0;
        Integer wrapperInteger = null;
        
        System.out.println("基本類型 int 默認值: " + primitiveInt);
        System.out.println("包裝類 Integer 默認值: " + wrapperInteger);
    }
    
    // 測試性能差異
    private static void testPerformance() {
        System.out.println("\n=== 測試性能 ===");
        // 測試基本類型
        long startTime = System.nanoTime();
        int sum1 = 0;
        for (int i = 0; i < 1000000; i++) {

            sum1 = i + 1;
        }
        long endTime = System.nanoTime();
        System.out.println("基本類型耗時: " + (endTime - startTime) + " 納秒");
        
        // 測試包裝類型
        startTime = System.nanoTime();
        Integer sum2 = 0;
        for (Integer i = 0; i < 1000000; i++) {
            sum2 += i;
        }
        endTime = System.nanoTime();
        System.out.println("包裝類型耗時: " + (endTime - startTime) + " 納秒");
    }
    
    // 測試Null處理
    private static void testNullHandling() {
        System.out.println("\n=== 測試Null處理 ===");
        Integer nullInteger = null;
        
        try {
            // 嘗試將null的Integer轉換為int
            int value = nullInteger;
            System.out.println("值為: " + value);
        } catch (NullPointerException e) {
            System.out.println("發生空指針異常：包裝類為null時不能自動拆箱為基本類型");
        }
    }
    
    // 測試裝箱拆箱
    private static void testBoxingUnboxing() {
        System.out.println("\n=== 測試裝箱拆箱 ===");
        // 自動裝箱
        Integer boxedInt = 100;    // 自動裝箱
        System.out.println("自動裝箱後的值: " + boxedInt);
        
        // 自動拆箱
        int unboxedInt = boxedInt;  // 自動拆箱
        System.out.println("自動拆箱後的值: " + unboxedInt);
        
        // 測試相等性
        Integer int1 = 127;
        Integer int2 = 127;
        Integer int3 = 200;
        Integer int4 = 200;
        
        System.out.println("127 == 127: " + (int1 == int2));  // true，因為IntegerCache
        System.out.println("200 == 200: " + (int3 == int4));  // false，超出Cache範圍
        System.out.println("200 equals 200: " + int3.equals(int4));  // true，使用equals比較
    }
    
    // 測試方法使用
    private static void testMethods() {
        System.out.println("\n=== 測試方法使用 ===");
        // Integer類的實用方法
        String numStr = "123";
        int parsedInt = Integer.parseInt(numStr);
        System.out.println("字符串轉換為整數: " + parsedInt);
        
        System.out.println("Integer整數最大值: " + Integer.MAX_VALUE);
        System.out.println("Integer整數最小值: " + Integer.MIN_VALUE);
        
        String binaryStr = Integer.toBinaryString(123);
        System.out.println("123的二進制表示: " + binaryStr);
        
        String hexStr = Integer.toHexString(123);
        System.out.println("123的十六進制表示: " + hexStr);
    }
}