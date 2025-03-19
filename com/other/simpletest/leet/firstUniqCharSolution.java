package com.other.simpletest.leet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//第一個不重複字符的位置: 0
//該字符是: l

public class firstUniqCharSolution {
	public int firstUniqChar(String s) {
		// 在這裡寫下你的解法

		
		
		Map<Character, Integer> charCount = new HashMap<>();
		//第次遍歷：計算字符的次數
		for (char c : s.toCharArray()) {
			if (charCount.containsKey(c)) {
				charCount.put(c, charCount.get(c) + 1);
			} else {
				//錯誤簡易未處理
				charCount.put(c, 1);

			}
		}
		// 第二次遍歷：找第一個只出現一次的字符
		for (int i = 0; i < s.length(); i++) {
			if (charCount.get(s.charAt(i)) == 1) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		firstUniqCharSolution solution = new firstUniqCharSolution();

		// 測試案例
		String[] tests = { "leetcode", "loveleetcode", "aabb" };

		for (String test : tests) {
			System.out.println("測試字串: " + test);
			//第一個不重複字符的位置
			int result = solution.firstUniqChar(test);
			if (result == -1) {
				System.out.println("沒有找到不重複的字符");
			} else {
				System.out.println("第一個不重複字符的位置: " + result);
				System.out.println("該字符是: " + test.charAt(result));
			}
			System.out.println("---------------");
		}
	}
}
