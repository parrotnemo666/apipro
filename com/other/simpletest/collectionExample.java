package com.other.simpletest;

import java.util.ArrayList;
import java.util.LinkedList;

public class collectionExample {
	
	public static void main(String[] args) {
		//ArrayList 練習 適合順序儲存
		ArrayList<Integer> scores = new ArrayList<>();
		scores.add(96);
		scores.add(40);
		scores.add(45);
		System.out.println("第二個成績:"+scores.get(1));
		
		for(Integer score:scores) {
			System.out.println("成績:"+score);	
		}
		//
		LinkedList<String> messagequence = new LinkedList<>();
		messagequence.addFirst("緊急通知");
		messagequence.addLast("一般消息");
		
		String urgentmsg = messagequence.removeFirst();
		String normalmsg = messagequence.removeLast();
		
		System.out.println("urgentmsg: "+urgentmsg);
		System.out.println("normalmsg: "+normalmsg);
	}

}
