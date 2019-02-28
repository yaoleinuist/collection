package com.lzhsite.leetcode.algoritom.practise.arraysAndMatrices;

/**
 * 一个数组元素在 [1, n] 之间，其中一个数被替换为另一个数，找出重复的数和丢失的数
 * 
 * 最直接的方法是先对数组进行排序，这种方法时间复杂度为 O(NlogN)。本题可以以 O(N) 的时间复杂度、O(1) 空间复杂度来求解。
 * 主要思想是通过交换数组元素，使得数组上的元素在正确的位置上。遍历数组，如果第 i 位上的元素不是 i + 1，那么一直交换第 i 位和 nums[i] -
 * 1 位置上的元素。
 *
 * 类似题目：
 * 448. Find All Numbers Disappeared in an Array (Easy)，寻找所有丢失的元素
 * 442. Find All Duplicates in an Array (Medium)，寻找所有重复的元素。
 * 
 * @author lzhcode
 *
 */
public class Set_Mismatch {

	public int[] findErrorNums(int[] nums) {
		for (int i = 0; i < nums.length; i++) {
			while (nums[i] != i + 1 && nums[nums[i] - 1] != nums[i]) {
				swap(nums, i, nums[i] - 1);
			}
		}
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != i + 1) {
				return new int[] { nums[i], i + 1 };
			}
		}
		return null;
	}

	private void swap(int[] nums, int i, int j) {
		int tmp = nums[i];
		nums[i] = nums[j];
		nums[j] = tmp;
	}
}
