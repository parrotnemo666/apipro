package com.other.simpletest.leet;

import java.util.Arrays;
import java.util.HashSet;

import org.glassfish.jersey.inject.hk2.JerseyClassAnalyzer;

public class IntersectionArrays {
	
	public int[] intersection(int[] nums1,int[] nums2) {
		HashSet<Integer> set = new HashSet<>();
		
		for(int num: nums1) {
			set.add(num);
		
		}
		HashSet<Integer> set2 = new HashSet<>();
		 for (int num : nums2) {
	            // 如果 num 在第一個 set 中存在，就是交集的元素
	            if (set.contains(num)) {
	            	set2.add(num);
	            }
	        }
	        
	        // 將結果轉換為數組
	        int[] result = new int[set2.size()];
	        int i = 0;
	        for (int num : set2) {
	            result[i++] = num;
	        }
	        
	        return result;
	    }
	
	
	
	
	 public static void main(String[] args) {
	        IntersectionArrays solution = new IntersectionArrays();
	        
	        // 測試案例1: 基本案例
	        int[] nums1 = {1, 2, 2, 1};
	        int[] nums2 = {2, 2};
	        System.out.println("測試案例1:");
	        System.out.println("數組1: " + Arrays.toString(nums1));
	        System.out.println("數組2: " + Arrays.toString(nums2));
	        System.out.println("交集結果: " + Arrays.toString(solution.intersection(nums1, nums2)));
	        System.out.println();
	        
	        // 測試案例2: 沒有交集的情況
	        int[] nums3 = {4, 5, 6};
	        int[] nums4 = {1, 2, 3};
	        System.out.println("測試案例2:");
	        System.out.println("數組1: " + Arrays.toString(nums3));
	        System.out.println("數組2: " + Arrays.toString(nums4));
	        System.out.println("交集結果: " + Arrays.toString(solution.intersection(nums3, nums4)));
	        System.out.println();
	        
	        // 測試案例3: 多個交集的情況
	        int[] nums5 = {4, 9, 5, 4, 9};
	        int[] nums6 = {9, 4, 9, 8, 4};
	        System.out.println("測試案例3:");
	        System.out.println("數組1: " + Arrays.toString(nums5));
	        System.out.println("數組2: " + Arrays.toString(nums6));
	        System.out.println("交集結果: " + Arrays.toString(solution.intersection(nums5, nums6)));
	    }

}
