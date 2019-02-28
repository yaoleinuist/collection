package com.lzhsite.leetcode.algoritom.practise.arraysAndMatrices;
/**
 * 找出数组中重复的数，数组值在 [1, n] 之间
 * @author lzhcode
 *
 */
public class Find_The_Duplicate_Number {
	/**
	 * 二分查找解法：
	 * @param nums
	 * @return
	 */
	public int findDuplicate(int[] nums) {
	     int l = 1, h = nums.length - 1;
	     while (l <= h) {
	         int mid = l + (h - l) / 2;
	         int cnt = 0;
	         for (int i = 0; i < nums.length; i++) {
	             if (nums[i] <= mid) cnt++;
	         }
	         //小于mid的数据偏多说明重复的数据在mid左边
	         if (cnt > mid) h = mid - 1;
	         //大于等于mid的数据偏多说明重复的数据在mid右边
	         else l = mid + 1;
	     }
	     return l;
	}
}
