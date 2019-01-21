package com.lzhsite.leetcode.algoritom.practise.linked;

import com.lzhsite.leetcode.algoritom.dataSructure.DLNode;

public class MyLinkedList {
	public DLNode first;
	public DLNode last;

	public boolean isEmpty() {
		return first == null;
	}

	public void print() {
		DLNode current = first;
		while (current != null) {
			System.out.print("[" + current.element + "] ");
			current = current.next;
		}
		System.out.println();
	}
	public void reverse_print() {

		DLNode current = first;
		DLNode before = null;
		System.out.println();
		System.out.println("反转后的列表数据:");
		while (current != null) {
			//last指向上一个节点
			last = before;
			//before指向当前节点
			before = current;
			//current指向下一个节点
			current = current.next;
			//当前节点的next是上一个节点
			before.next = last;
		}
		//此时的before已经指向最后一个节点
		current = before;
		while (current != null) {
			System.out.print("[" + current.element + "] ");
			current = current.next;
		}
		System.out.println();
	}
	public void insertLast(int data) {
		if(isEmpty()){
			DLNode newNode = new DLNode(data,null,null);
			first =newNode;
			last =newNode;
		}else{
			DLNode newNode = new DLNode(data,first,null);
			last.next=newNode;
			last=newNode;
		}
		
	}
}
