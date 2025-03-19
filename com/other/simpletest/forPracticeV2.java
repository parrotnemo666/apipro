package com.other.simpletest;

public class forPracticeV2 {

	public static void main(String[] args) {
		
		int[] numbers = {1,2,3,4,5};
		String[] fruits = {"apple","banana","orange","grape"}; 
		
		System.out.println("===一般FOR迴圈===");
		for(int i = 0; i < numbers.length;i++) {
//			System.out.print(numbers[i] + " ");
			
			numbers[i] =numbers[i]*2;
			
			System.out.print(numbers[i] + " ");
		}
		System.out.println();

		
		System.out.println("===增強型迴圈===");
		for(int number :numbers) {
			
			System.out.println(number+" ");
		}
		System.out.println();
		
		//如果今天需要的話
		System.out.println("===需要裡面的一個值時===");
		for(int i = 0; i<fruits.length;i++) {
			
			System.out.printf("第 %d 個水果是:%s \n",i+1,fruits[i]);
		}
		
		
		System.out.println("===全部都印出來===");
	
		for(String fruit:fruits) {
			System.out.println("水果:"+fruit);
			
			
		}
		//數字轉string
		int num = 123;
		String str1 = String.valueOf(num);
		System.out.println(str1);
		
		//文字轉數字
		String str2 = "456";
		Integer num2= Integer.valueOf(str2);
	    System.out.println(num2);
	    
	    //轉成double
	    String str3 = "456";
	    Double doublevalue = Double.valueOf(str3);
	    System.out.println(doublevalue);
	    
	    Integer num1 = 123;
	    String str4 = String.valueOf(num1);
	    System.out.println(str4);
	    //傳入空值會發生什麼事情
//	    Integer num3 = Integer.valueOf(null);
//	    System.out.println(num3);
	    
	    Integer num6 = null;
	    String str33 = String.valueOf(num6);
	    System.out.println(str33);
	    //這樣出來會是null
	} 
	
	

}
