package com.lzhsite.leetcode.algoritom.practise.arraysAndMatrices;
/**
 * 找出数组中最长的连续 1(数组不是0就是1)
 * @author lzhcode
 *
 */
public class Max_Consecutive_Ones {
	public int findMaxConsecutiveOnes(int[] nums) {
	    int max = 0, cur = 0;
	    for (int x : nums) {
	        cur = x == 0 ? 0 : cur + 1;
	        max = Math.max(max, cur);
	    }
	    return max;
	}
}
