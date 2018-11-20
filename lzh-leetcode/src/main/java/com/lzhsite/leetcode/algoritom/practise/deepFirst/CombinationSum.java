package com.lzhsite.leetcode.algoritom.practise.deepFirst;

import java.util.ArrayList;
import java.util.List;

/*
 *  题目意思：人民币找钱问题
	Given a set of candidate numbers (candidates) (without duplicates) and a target number (target), find all unique combinations in candidates where the candidate numbers sums to target.
	
	The same repeated number may be chosen from candidates unlimited number of times.
	
	Note:
	
	All numbers (including target) will be positive integers.
	The solution set must not contain duplicate combinations.
*/
public class CombinationSum {

	public static int target = 20;
	public static int nums[] = { 1, 2, 10, 5 };
	public static List<List<Integer>> sumResult=new  ArrayList<List<Integer>>();
	
	public static void main(String[] args) {
	
	    List<Integer> result = new ArrayList();
	    //dfs(0,target,result);
	    dfs2(0,target);
	}

	/**
	 * 解法1
	 * @param lastIndex  上次所选的数组下标(保证递归的时候只能随着递增)
	 * @param target     剩余可减去的数字大小
	 * @param result     每次所选的结果
	 */
	private static void dfs(int lastIndex, int target,List<Integer> result) {
		// TODO Auto-generated method stub
		if(target==0){
			for (Integer num : result) {
				System.out.print(num+" ");
			}
			System.out.println();
			sumResult.add(result);
			return;
		}else if(target < 0){
			return;
		}
		
		for (int i = lastIndex; i < nums.length; i++) {
			
			List result2 = new ArrayList(result);
			result2.add(nums[i]);
			
			dfs(i, target-nums[i], result2);
			
		}
	
	}

	/**
	 * 解法2
	 * @param lastIndex  上次所选的数组下标(保证递归的时候只能随着递增)
	 * @param target     剩余可减去的数字大小
	 * @param result     每次所选的结果
	 */
	public static int length = 0;
	public static int result[] = new int[100];
	private static void dfs2(int lastIndex, int target) {
		if(target==0){
			for (int i = 0; i < length; i++) {
				System.out.print(result[i]+" ");
			}
			System.out.println();
			return;
		}else if(target < 0 || lastIndex >= nums.length){
			return;
		}
		
		//选中当前下标的数字
		result[length]= nums[lastIndex];
		length++;
		
		dfs2(lastIndex, target-nums[lastIndex]);
		//同一层次还原递归所需增加的length
		length--; 
		//不选中当前下标的数字
		dfs2(lastIndex+1, target);
		
	}
	
}
