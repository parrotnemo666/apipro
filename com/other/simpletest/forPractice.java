package com.other.simpletest;

public class forPractice {

	public static void main(String[] args) {
		System.out.println("====九九乘法表練習====");
		for (int i = 1; i < 10; i++) {
			for (int j = 2; j < 10; j++) {

				System.out.printf("%d*%d= %2d ", j, i, i * j);

			}
			System.out.println(); // 換行

		}
		System.out.println("====左右對齊練習====");
		int x = 3;
		int y = 4;
		int result = x * y;

		System.out.printf("基本格式:  %d*%d=%d\n", x, y, result);
		System.out.printf("右對齊格式: %d*%d=%3d\n", x, y, result);
		System.out.printf("左對齊格式: %d*%d=%-3d", x, y, result);
//		System.out.printf("基本格式:  %d*%d=");

		System.out.println("====金字塔練習====");
		for (int i = 0; i < 10; i++) {
			for (int j = 1; j <= i; j++) {
				System.out.print("*" + " ");
//				System.out.print(j + " ");
			}
			System.out.println();
		}
		System.out.println();

		// 印出 1到5
		System.out.println("印出 1到5");
		for (int i = 1; i < 5; i++) {
			System.out.print(i + " ");
		}
		// 印出 5到1
		System.out.println("印出 5到1");
		for (int i = 5; i < 2; i--) {
			System.out.print(i + " ");
		}
		// 印出1~10的偶數
		System.out.println("印出1~10的偶數");
		for (int i = 2; i <= 10; i += 2) {
			System.out.print(i + " ");
		}
		System.out.println("\n");

		System.out.println("印出1~10的奇數");
		for (int i = 1; i <= 10; i = i + 2) {
			System.out.print(i + " ");
		}
		System.out.println("\n");

		System.out.println("計算1+2+3+4+5的總和");

		int sum = 0;
		for (int i = 1; i <= 5; i++) {
			sum = sum + i;
			System.out.print(i);
			if (i < 5)
				System.out.print(" + ");
		}
		System.out.println(" = " + sum + "\n");

		System.out.println("====印出5的倍數====");
		for (int i = 0; i <= 50; i = i + 5) {
			System.out.print(i + " ");

		}
		System.out.println("\n");

		System.out.println("====金字塔練習直角三角形====");
		for (int i = 0; i < 10; i++) {
			for (int j = 1; j <= i; j++) {

				System.out.print("*" + " "); // 星星三角形
//			System.out.print(j + " "); //數字三角形
			}
			System.out.println();
		}
		System.out.println();

		System.out.println("====印出倒直角三角形====");
		for (int i = 5; i >= 1; i--) {
			for (int j = 1; j <= i; j++) {
				System.out.print("* ");
			}
			System.out.println();

		}

		// 1. print() - 基本輸出，不換行
		System.out.println("1. print() 示例：");
		System.out.print("第一個");
		System.out.print("第二個");
		System.out.print("第三個");
		System.out.println("\n"); // 額外換行，區隔示例

		// 2. println() - 輸出後換行
		System.out.println("2. println() 示例：");
		System.out.println("第一個");
		System.out.println("第二個");
		System.out.println("第三個");
		System.out.println();

		// 3. printf() - 格式化輸出，不換行
		System.out.println("3. printf() 示例：");
		int num = 42;
		double pi = 3.14159;
		String name = "小明";

		System.out.printf("數字是：%d\n", num);
		System.out.printf("圓周率是：%.2f\n", pi);
		System.out.printf("名字是：%s\n", name);
		System.out.printf("綜合格式：%s 有 %d 個蘋果，價格是 %.1f 元%n", name, num, pi);
		System.out.println();

		// 4. format() - 與 printf() 相同
		System.out.println("4. format() 示例：");

		System.out.format("名字是：%s，年齡是：%d%n", name, num);
		System.out.println();
		
		
		System.out.println("====金字塔正常版====");

		int height = 6;

		for (int i = 1; i <= height; i++) {
			
			//隱藏的部分
			for (int j = 1; j <= height-i; j++) {
				System.out.print("* ");
			}
//			//金字塔本體  k <= 2*i-1這曾有幾個
//			for (int k = 1; k <= 2*i-1; k++) {
//				System.out.print("+ ");
//			}
			System.out.println();

		}
		
		int[] numbers = {1,2,3,4,5};
		String[] fruit = {"apple","banana","orange","grape"}; 
 	}

}
