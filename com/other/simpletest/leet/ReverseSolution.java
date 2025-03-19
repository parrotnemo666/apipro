package com.other.simpletest.leet;



public class ReverseSolution {

	public int reverse(int x) {
		StringBuilder sb = new StringBuilder(String.valueOf(x));
		System.out.println("sb:"+sb);
		String reversed = sb.reverse().toString();
		
		// 等等把他改成values
		System.out.println("reversed:"+reversed);
		
		try {
			int result = Integer.valueOf(reversed);
			
			return x < 0 ? -result : result; //不然會產生問題 如果 X = -123 轉成 321 這邊會出來321而已不會有-號 
		} catch (Exception e) {
			// TODO: handle exception
			return 0; //資料發生溢出
		}


	}

	public static void main(String[] args) {
		ReverseSolution solution = new ReverseSolution();

		// 測試案例
		int[] tests = { 123, 123666, 120, 1534266666 }; // 後面再加

		// 把我的反轉
		for (int test : tests) {
			System.out.println("\n測試數字: " + test);
			System.out.println("反轉後: " + solution.reverse(test));
		}
	}
}