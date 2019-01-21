package com.lzhsite.leetcode.algoritom.practise.linked;

import com.lzhsite.leetcode.algoritom.dataSructure.DLNode;

//有两个单链表，代表两个非负数，每一个节点代表一个数位，数字是反向存储的，
//即第一个结点表示最低位，最后一个结点表示最高位。求两个数的相加和，并且以链表形式返回。
//举例:输入链表 1->8->3->6 和链表 1->2->3->8->4 最后得到链表 2->0->7->4->5

public class 单链表两数相加 {

	
	public static void main(String args[]){
	 
		DLNode node1 = new DLNode();
		node1.element = 1;  
  
		node1.next = new DLNode();  
		node1.next.element = 8;  
  
		node1.next.next = new DLNode();  
		node1.next.next.element = 3;  
  
		node1.next.next.next = new DLNode();  
		node1.next.next.next.element = 6;  
		
		
		DLNode node2 = new DLNode();
		node2.element = 1;  
  
		node2.next = new DLNode();  
		node2.next.element = 2;  
  
		node2.next.next = new DLNode();  
		node2.next.next.element = 3;  
  
		node2.next.next.next = new DLNode();  
		node2.next.next.next.element = 8;  
		
		node2.next.next.next.next = new DLNode();  
		node2.next.next.next.next.element = 4;  
		
		DLNode node3 = new DLNode();
		node3 = addTwoLists(node1, node2);
		
		//打印我们返回的链表结果
		while(node3 != null){
			System.out.print(node3);
			node3 = node3.next;
			
		}
 
	}
	
 
	public static DLNode addTwoLists(DLNode head1, DLNode head2){
		if(head1 == null || head2 == null){
			return null;
		}
		
		DLNode head3 = new DLNode();
		// 保存头结点，最后返回的时候用
		DLNode storeNode = head3;
		int flag = 0;
		int sum = 0;
		
		while(head1 != null && head2 != null){
			sum =(int)head1.element + (int)head2.element + flag;
			flag = 0;
			head3.element = sum % 10;
			flag =sum/10;
				
			 		
			head1 = head1.next;
			head2 = head2.next;
			
			//创建一个新的结点,前提是,两个链表都不是到达最后的元素时
			if(head1 != null && head2 != null){
				DLNode newnode = new DLNode();
				head3.next = newnode;
				head3 = newnode;
			}
		}
		
		// 如果有一个为空的情况下执行
		if(head1 == null){
			head3.next = head2;
		}
		if(head2 == null){
			head3.next = head1;
		}
		
		// 也就是说最后一位相加还有进位，要创建一个新的结点
		if(flag == 1){
			DLNode lastnode = new DLNode();
			if(head3.next != null){
				lastnode.element = (int)head3.next.element+1;
			}else{
				lastnode.element = 1;
			}
			
			head3.next = lastnode;
		}		
		return storeNode;
	}

 
}
