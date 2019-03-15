package com.lzhsite.leetcode.algoritom.practise.linked;

import com.lzhsite.leetcode.algoritom.dataSructure.DLNode;

//https://mp.weixin.qq.com/s/hFtlQp0_0MQ7cbRsmjhLbQ

public class 输出此链表中的倒数第K个节点 {

   /*	
    * 2.2.1 解题思想
    * (1)遍历单链表，遍历同时得出链表长度 N 。
    * (2)再次从头遍历，访问至第 N - K 个节点为所求节点。
	*/
	/* 计算链表长度 */
	int listLength(DLNode pHead) {
		int count = 0;
		DLNode pCur = pHead.next;
		if (pCur == null) {
			System.out.println("error");
		}
		while (pCur != null) {
			count++;
			pCur = pCur.next;
		}
		return count;
	}

	/* 查找第k个节点的值 */
	DLNode searchNodeK(DLNode pHead, int k) {
		int i = 0;
		DLNode pCur = pHead;
		// 计算链表长度
		int len = listLength(pHead);
		if (k > len) {
			System.out.println("error");
		}
		// 循环len-k+1次
		for (i = 0; i < len - k + 1; i++) {
			pCur = pCur.next;
		}
		return pCur;// 返回倒数第K个节点
	}

	/*
	 * 2.3 递归法
	 * 
	 * 2.3.1 解题思想 
	 * （1）定义num = k 
	 * （2）使用递归方式遍历至链表末尾。 
	 * （3）由末尾开始返回，每返回一次 num 减 1 （4）当
	 * num 为 0 时，即可找到目标节点
	 */
	int num;// 定义num值

	DLNode findKthTail(DLNode pHead, int k) {
		num = k;
		if (pHead == null)
			return null;
		// 递归调用
		DLNode pCur = findKthTail(pHead.next, k);
		if (pCur != null)
			return null;
		else {
			num--;// 递归返回一次，num值减1
			if (num == 0)
				return pHead;// 返回倒数第K个节点
			return null;
		}
	}
}
