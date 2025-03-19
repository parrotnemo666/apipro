package com.other.simpletest.leet;

import java.util.HashSet;

public class leetCodeSetsolution {
		public boolean containsDuplication(int[] nums) {

			// 需要先設定一個set讓已經檢查過的元素儲存在裡面
			HashSet<Integer> seen = new HashSet<>();

			for (int num : nums) {
				if (!seen.add(num)) {
					return true;
				}

			}
			return false;
		}

	

	public static void main(String[] args) {
		leetCodeSetsolution solution = new leetCodeSetsolution();

		// 測試案例1: 有重複的數組
		int[] nums1 = { 1, 2, 3, 1 };
		System.out.println("測試案例1: 數組 [1,2,3,1]");
		System.out.println("預期結果: true (因為1重複)");
		System.out.println("實際結果: " + solution.containsDuplication(nums1));
		System.out.println();

		// 測試案例2: 沒有重複的數組
		int[] nums2 = { 1, 2, 3, 4 };
		System.out.println("測試案例2: 數組 [1,2,3,4]");
		System.out.println("預期結果: false (沒有重複)");
		System.out.println("實際結果: " + solution.containsDuplication(nums2));
		System.out.println();

		// 測試案例3: 空數組
		int[] nums3 = {};
		System.out.println("測試案例3: 空數組 []");
		System.out.println("預期結果: false (空數組沒有重複)");
		System.out.println("實際結果: " + solution.containsDuplication(nums3));
		System.out.println();

		// 測試案例4: 多個重複的數組
		int[] nums4 = { 1, 1, 1, 3, 3, 4, 3, 2, 4, 2 };
		System.out.println("測試案例4: 數組 [1,1,1,3,3,4,3,2,4,2]");
		System.out.println("預期結果: true (多個數字重複)");
		System.out.println("實際結果: " + solution.containsDuplication(nums4));
	}

}
