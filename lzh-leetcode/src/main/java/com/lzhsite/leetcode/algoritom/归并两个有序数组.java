package com.lzhsite.leetcode.algoritom;

import java.util.Arrays;

/*
	Input:
	nums1 = [1,2,3,0,0,0], m = 3
	nums2 = [2,5,6],       n = 3
	Output: [1,2,2,3,5,6]
	题目描述：把归并结果存到第一个数组上。

*/
public class 归并两个有序数组 {
	/**
	 * 思路：
	 * 需要从尾开始遍历，否则在 nums1 上归并得到的值会覆盖还未进行归并比较的值。
	 * 新建一个以两个集合长度之和为长度的新数组，nums1从m开始，nums2从最右边开始，把大的放入新集合，并用变量标记后一位置，
     * 每次比较都是比较的最右边未比较过的元素（通过变量），循环比较，如果其中一个数组已经放完了就放另一个数组的数，
     * 直至两个集合都遍历结束
     * 
	 * @param nums1
	 * @param m
	 * @param nums2
	 * @param n
	 */
	public static void merge(int[] nums1, int m, int[] nums2, int n) {
		int index1 = m - 1, index2 = n - 1;
		int indexMerge = m + n - 1;
		while (index1 >= 0 || index2 >= 0) {
			if (index1 < 0) {
				nums1[indexMerge--] = nums2[index2--];
			} else if (index2 < 0) {
				nums1[indexMerge--] = nums1[index1--];
			} else if (nums1[index1] > nums2[index2]) {
				nums1[indexMerge--] = nums1[index1--];
			} else {
				nums1[indexMerge--] = nums2[index2--];
			}
		}
	}
/**
 * 思路：新建一个以两个集合长度之和为长度的新数组，从两数组最左边开始比起，把小的放入新集合，并用变量标记后一位置，
 * 每次比较都是比较的最左边未比较过的元素（通过变量），循环比较，直至其中有一个集合遍历结束，
 * 将另一个集合剩余部分加入新集合中
 * @param nums1
 * @param nums2
 */
	public static void merge2(int[] nums1, int[] nums2) {

		// 变量用于存储两个集合应该被比较的索引（存入新集合就加一）
		int a = 0;
		int b = 0;
		int[] num3 = new int[nums1.length + nums2.length];
		for (int i = 0; i < num3.length; i++) {
			if (a < nums1.length && b < nums2.length) { // 两数组都未遍历完，相互比较后加入
				if (nums1[a] > nums2[b]) {
					num3[i] = nums2[b];
					b++;
				} else {
					num3[i] = nums1[a];
					a++;
				}
			} else if (a < nums1.length) { // nums2已经遍历完，无需比较，直接将剩余nums1加入
				num3[i] = nums1[a];
				a++;
			} else if (b < nums2.length) { // nums1已经遍历完，无需比较，直接将剩余nums2加入
				num3[i] = nums2[b];
				b++;
			}
		}
		System.out.println("排序后:" + Arrays.toString(num3));
	}

	public static void main(String[] args) {
		int[] num1 = new int[] { 1, 2, 4, 6, 7, 123, 411, 5334, 1414141, 1314141414 };
		int[] num2 = new int[] { 0, 2, 5, 7, 89, 113, 5623, 6353, 134134 };
		merge2(num1,num2);
	}

}
