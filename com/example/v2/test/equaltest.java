package com.example.v2.test;



public class equaltest {

	public static void main(String[] args) {
		String str1 = "hello";
		String str2 = "hello";
		System.out.println("case1 :stringpool比較");
		System.out.println("str1"+str1);
		System.out.println("str2"+str2);
		System.out.println("str1 == str2:"+(str1 == str2));
		System.out.println("str1 str2:"+str1.equals(str2));
		System.out.println();
		
		
		String str3 = new String("hello") ;
		String str4 = new String("hello");
		System.out.println("case :new string()比較");
		System.out.println("str3"+str3);
		System.out.println("str4"+str4);
		System.out.println("str3 == str4"+(str3 == str4));
		System.out.println("str3 str4"+str3.equals(str4));
		System.out.println();
		
		
	}

}
