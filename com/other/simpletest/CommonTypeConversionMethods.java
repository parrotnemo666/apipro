package com.other.simpletest;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class CommonTypeConversionMethods {
	public static void main(String[] args) {
		// 1. toString() 系列
		System.out.println("=== toString 相關方法 ===");
		Integer num = 100;
		System.out.println("toString(): " + num.toString());
		System.out.println("Arrays.toString(): " + Arrays.toString(new int[] { 1, 2, 3 }));
		System.out.println("Arrays.deepToString(): " + Arrays.deepToString(new int[][] { { 1, 2 }, { 3, 4 } }));

		// 2. valueOf() 系列
		System.out.println("\n=== valueOf 相關方法 ===");
		String strNum = "100";
		System.out.println("Integer.valueOf(): " + Integer.valueOf(strNum));
		System.out.println("Integer.valueOf():  "+Integer.valueOf(strNum));
		System.out.println("Boolean.valueOf(): " + Boolean.valueOf("true"));
		System.out.println("Double.valueOf(): " + Double.valueOf("100.5"));

		// 3. parseXxx() 系列
		System.out.println("\n=== parse 相關方法 ===");
		System.out.println("Integer.parseInt(): " + Integer.parseInt("100"));
		System.out.println("Integer.parseInt(): " + Integer.parseInt("100"));
		System.out.println("Double.parseDouble(): " + Double.parseDouble("100.5"));
		System.out.println("Boolean.parseBoolean(): " + Boolean.parseBoolean("true"));

		// 4. format() 系列
		System.out.println("\n=== format 相關方法 ===");
		double amount = 1234567.89;
		System.out.println("String.format(): " + String.format("%.2f", amount));
		DecimalFormat df = new DecimalFormat("#,###.##");
		System.out.println("DecimalFormat: " + df.format(amount));

		// 5. getXxx() 系列（基本型別取值）
		System.out.println("\n=== get 相關方法 ===");
		Integer intObj = new Integer(100);
		System.out.println("intValue(): " + intObj.intValue());
		Double doubleObj = new Double(100.5);
		System.out.println("doubleValue(): " + doubleObj.doubleValue());

		// 6. 進制轉換方法
		System.out.println("\n=== 進制轉換方法 ===");
		int number = 255;
		System.out.println("toHexString(): " + Integer.toHexString(number));
		System.out.println("toBinaryString(): " + Integer.toBinaryString(number));
		System.out.println("toOctalString(): " + Integer.toOctalString(number));

		// 7. 字元與字串轉換
		System.out.println("\n=== 字元與字串轉換 ===");
		char[] chars = { 'H', 'e', 'l', 'l', 'o' };
		System.out.println("new String(): " + new String(chars));
		String str = "Hello";
		System.out.println("toCharArray(): " + Arrays.toString(str.toCharArray()));

		// 8. 集合轉換方法
		System.out.println("\n=== 集合轉換方法 ===");
		List<String> list = Arrays.asList("A", "B", "C");
		System.out.println("Arrays.asList(): " + list);
		String[] array = list.toArray(new String[0]);
		System.out.println("toArray(): " + Arrays.toString(array));

		// 9. StringBuilder/StringBuffer
		System.out.println("\n=== StringBuilder 方法 ===");
		StringBuilder sb = new StringBuilder();
		sb.append("Hello").append(" World");
		System.out.println("StringBuilder: " + sb.toString());
	}
}