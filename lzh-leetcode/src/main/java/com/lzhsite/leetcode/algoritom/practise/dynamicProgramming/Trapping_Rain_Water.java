package com.lzhsite.leetcode.algoritom.practise.dynamicProgramming;

/**
 * 题目来源：LeetCode 42. 接雨水 题目描述：给定 n 个非负整数表示每个宽度为 1 的柱子的高度图，计算按此排列的柱子，下雨之后能接多少雨水
 * 上面是由数组 [0,1,0,2,1,0,1,3,2,1,2,1] 表示的高度图，在这种情况下，可以接 6 个单位的雨水（蓝色部分表示雨水） 示例： 输入:
 * [0,1,0,2,1,0,1,3,2,1,2,1]输出: 6
 * 
 * 思路：这个单纯的遍历其实也能出来，但是要考虑的情况比较多， 对每一个柱子能存多少水求和这种方法比较简单，
 * 这样只需要获取这个柱子左边的最高高度和这个柱子右边的最高高度， 2者的最小值减去柱子的高度就是这个柱子的存水量
 */
public class Trapping_Rain_Water {

	public int trap(int[] height) {
		int sum = 0;
		for (int i = 0; i < height.length; i++) {
			int maxLeft = 0, maxRight = 0;
			for (int left = 0; left < i; left++) {
				maxLeft = Math.max(maxLeft, height[left]);
			}
			for (int right = i + 1; right < height.length; right++) {
				maxRight = Math.max(maxRight, height[right]);
			}
			int temp = Math.min(maxLeft, maxRight) - height[i];
			if (temp > 0)
				sum += temp;
		}
		return sum;
	}

	/**
	 * 每次都要算某个柱子的左右最值，时间复杂度是O(n2)，能不能把算左右最值的效率提高呢？ 
	 * 这就用到动态规划了，假如说
	 * 我们用函数f(n)，表示到第n个柱子（包括第n个柱子）左边的最大值，
	 * 则f(n)=max(f(n-1)，height[n])，其中height[n]为第n个柱子的高度，右边同理
	 */

	public int trap2(int[] height) {
		int sum = 0;
		int len = height.length;
		if (len == 0)
			return 0;
		int[] maxLeft = new int[len];
		int[] maxRight = new int[len];
		maxLeft[0] = height[0];
		for (int i = 1; i < len; i++) {
			maxLeft[i] = Math.max(height[i], maxLeft[i - 1]);
		}
		maxRight[len - 1] = height[len - 1];
		for (int i = len - 2; i >= 0; i--) {
			maxRight[i] = Math.max(height[i], maxRight[i + 1]);
		}
		for (int i = 0; i < height.length; i++) {
			sum += Math.min(maxLeft[i], maxRight[i]) - height[i];
		}
		return sum;
	}

}
