package com.lzhsite.technology.algoritom.practise;

import java.util.Scanner;
/** 
 * 牛牛定义排序子序列为一个数组中一段连续的子序列,并且这段子序列是非递增或者非递减排序的。牛牛有一个长度为n的整数数组A, 
 * 他现在有一个任务是把数组A分为若干段排序子序列,牛牛想知道他最少可以把这个数组分为几段排序子序列. 
 * 如样例所示,牛牛可以把数组A划分为[1,2,3]和[2,2,1]两个排序子序列,至少需要划分为2个排序子序列,所以输出2 
 *  
 * 
 */ 
public class 数字排序子序列 {

	private static int a[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

	// 求所有子序列
	public static void main(String[] args) {

		// 输入包括一个整数n(1 ≤ n ≤ 1,000,000,000)
		Scanner s = new Scanner(System.in);
		// 输入的第一行为一个正整数n(1 ≤ n ≤ 10^5)
		int n = s.nextInt();
		// 第二行包括n个整数A_i(1 ≤ A_i ≤ 10^9),表示数组A的每个数字。
		int[] A = new int[n];
		for (int i = 0; i < n; i++) {
			A[i] = s.nextInt();
		}
		System.out.println(sonCount(A, n));
	}

	static int sonCount(int[] A, int n) {
		int flag = 0;// 递减为-1，相等0，递增为1，默认相等
		int result = 1;// 默认为一个序列
		for (int i = 1; i < n; i++) {
			// 如果后一个大于前一个，即递增
			if (A[i] > A[i - 1]) {
				// 如果原来是相等,标志为递增
				if (flag == 0)
					flag = 1;
				// 如果原来就是递减，增加一个子序列，标志恢复默认
				else if (flag == -1) {
					result++;
					flag = 0;
				}
				//// 如果后一个小于前一个，即递减
			} else if (A[i] < A[i - 1]) {
				// 如果原来是相等,标志为递减
				if (flag == 0)
					flag = -1;
				// 如果原来是递增,增加一个子序列，标志恢复默认
				else if (flag == 1) {
					result++;
					flag = 0;
				}
			}
			// 其他情况就是往下继续遍历
		}
		return result;
	}
	
	
	//数组所有子序列
	static void allsubsqe(){
		//i表示偏移量,j表示子序列的起始下标,k表示要打印子序列的所有下标
		for (int i = 1; i <= a.length; i++) {
			for (int j = 0; j <=a.length-i; j++) {
				for (int k = j; k <j+i; k++) {
					System.out.print(a[k]+" ");
				}
				System.out.println();
			}
			
		}
	}
	

}
