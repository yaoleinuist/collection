package com.lzhsite.leetcode.algoritom.practise.linked;

import java.io.IOException;
import java.util.Random;

import com.lzhsite.leetcode.algoritom.dataSructure.DLNode;

public class 单链表逆序  {
 
	
	public static void main(String args[]) throws IOException {
		Random rand = new Random();
		MyLinkedList list = new MyLinkedList();
		for (int i = 0; i < 12; i++) {
			list.insertLast((Math.abs(rand.nextInt(50))) + 50);
		}
		list.print();
		list.reverse_print();
	}
}

 
