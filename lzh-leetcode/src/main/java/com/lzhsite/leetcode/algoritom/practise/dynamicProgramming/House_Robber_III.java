package com.lzhsite.leetcode.algoritom.practise.dynamicProgramming;

import java.util.HashMap;

import com.lzhsite.leetcode.algoritom.TestBinaryTreeUtil;
import com.lzhsite.leetcode.algoritom.dataSructure.tree.BinTreeNode;
import com.lzhsite.leetcode.algoritom.dataSructure.tree.BinaryTreeLinked;

/**
     题意:偷东西不能偷相邻树节点的值不能偷,求能偷的最大值(树形dp)
   Example 1:
	
	Input: [3,2,3,null,3,null,1]
	
	     3
	    / \
	   2   3
	    \   \ 
	     3   1
	
	Output: 7 
	Explanation: Maximum amount of money the thief can rob = 3 + 3 + 1 = 7.
	Example 2:
	
	Input: [3,4,5,1,3,null,1]
	
	     3
	    / \
	   4   5
	  / \   \ 
	 1   3   1
	
	Output: 9
	Explanation: Maximum amount of money the thief can rob = 4 + 5 = 9.
 * @author lzhcode
 *
 */
public class House_Robber_III {
	
	
	
	HashMap<BinTreeNode, Integer> canRobMap =new HashMap<>();
	HashMap<BinTreeNode, Integer> uncanRobMap =new HashMap<>();
	
	
	public int dfs(BinTreeNode treeNode,Boolean canRob){
		
		int max =-1000;
	
		if(canRob && canRobMap.containsKey(treeNode)){
			return canRobMap.get(treeNode);
		}
		if(!canRob && uncanRobMap.containsKey(treeNode)){
			return uncanRobMap.get(treeNode);
		}
		
		if(treeNode.isLeaf()){
			return (int)treeNode.getData();
		}
		
		if(canRob){
			max=Math.max(max,(int)treeNode.getData()+dfs(treeNode.getLChild(),false)+dfs(treeNode.getRChild(),false));
		}
		
		max=Math.max(max,dfs(treeNode.getLChild(),true)+dfs(treeNode.getRChild(),true));
		
		
		if(canRob){
			canRobMap.put(treeNode, max);
		}else{
			uncanRobMap.put(treeNode, max);
		}
		
		return max;
	}
	
	
	public static void main(String[] args) {
		
		House_Robber_III house_Robber_III =new House_Robber_III();
		
		int[] datas = new int[]{1,2,3,4,5,6,7,8,9};
		BinTreeNode treeNode=TestBinaryTreeUtil.creatBinaryTree(datas);
		BinaryTreeLinked binaryTreeLinked =new BinaryTreeLinked(treeNode);
		
		
		TestBinaryTreeUtil.printElement(binaryTreeLinked.preOrder());
		//System.out.println(Math.max(house_Robber_III.dfs(treeNode, true), house_Robber_III.dfs(treeNode, false)));
		
	}
	
	

}
