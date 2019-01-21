package com.lzhsite.leetcode.algoritom.practise.linked;

import com.lzhsite.leetcode.algoritom.dataSructure.DLNode;
//使用双指针，一个指针每次移动一个节点，一个指针每次移动两个节点，
//如果存在环，那么这两个指针一定会相遇。
public class 链表有环 {
	
	public boolean hasCycle(DLNode head) {
	    if (head == null) {
	        return false;
	    }
	    DLNode l1 = head, l2 = head.getNext();
	    while (l1 != null && l2 != null && l2.getNext() != null) {
	        if (l1 == l2) {
	            return true;
	        }
	        l1 = l1.getNext();
	        l2 = l2.getNext().getNext();
	    }
	    return false;
	}
}
