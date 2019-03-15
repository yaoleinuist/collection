package com.lzhsite.leetcode.algoritom.practise.arraysAndMatrices;
/**
 * 把数组中的 0 移到末尾(easy)
 * For example, given nums = [0, 1, 0, 3, 12], after calling your function, nums should be [1, 3, 12, 0, 0].
 * @author lzhcode
 *
 */
public class Move_Zeroes {
	public void moveZeroes(int[] nums) {
		//不等于0时idx不断自增
	    int idx = 0; 
	    for (int num : nums) {
	        if (num != 0) {
	            nums[idx] = num;
	            idx++;
	        }
	    }
	    while (idx < nums.length) {
	        nums[idx] = 0;
	        idx++;
	    }
	}
}
