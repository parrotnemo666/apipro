package com.example.v2.test;

import com.ibm.db2.jcc.am.s;

public class StringMethodssubstringDemo {
	public static void main(String[] args) {
		// 1. lastIndexOf() 方法演示
		System.out.println("=== lastIndexOf() 方法演示 ===");
		String maxId = "20241022-0003";

		int position = maxId.lastIndexOf("-");
		System.out.println("原始字符串: " + maxId);
		System.out.println("'-' 的位置: " + position);
		System.out.println("字符串索引: 0123456789012");
		System.out.println("           " + maxId);
		System.out.println("           ↑");
		System.out.println("           位置 8");

		// 2. substring() 方法演示
		System.out.println("\n=== substring() 方法演示 ===");
		String sequence = maxId.substring(position + 1);
		System.out.println("提取後的序號: " + sequence);
		System.out.println("提取過程說明:");
		System.out.println("原始字符串:  " + maxId);
		System.out.println("開始位置:    " + (position + 1));
		System.out.println("提取結果:    " + sequence);

		// 3. 更多 lastIndexOf() 的例子
		System.out.println("\n=== 更多 lastIndexOf() 例子 ===");
		String text = "file-name-with-multiple-dashes.txt";
		System.out.println("文字: " + text);
		System.out.println("最後一個 '-' 的位置: " + text.lastIndexOf("-")); // 找最後一個
		System.out.println("第一個 '-' 的位置: " + text.indexOf("-")); // 找第一個

		// 4. 更多 substring() 的例子
		System.out.println("\n=== 更多 substring() 例子 ===");
		String example = "Hello-World-Test";
		System.out.println("完整字符串: " + example);
		System.out.println("substring(0, 5): " + example.substring(0, 5)); // 從開始到指定位置
		System.out.println("substring(6, 11): " + example.substring(6, 11)); // 指定範圍
		System.out.println("substring(12): " + example.substring(12)); // 從指定位置到結束
		
		String S = "ABCDEFG";
		String S1 = S.substring(5);
		String S2 = S.substring(0,5);
		System.out.println(S1); 
		System.out.println(S2); 
	}
}

