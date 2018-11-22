package com.lzhsite.leetcode.algoritom.practise.deepFirst;

import java.util.Arrays;

import com.lzhsite.leetcode.algoritom.TestArrUtil;

/*
       题意:在矩阵里寻找最长递增路径 hard
	Given an integer matrix, find the length of the longest increasing path.
	
	From each cell, you can either move to four directions: left, right, up or down. You may NOT move diagonally or move outside of the boundary (i.e. wrap-around is not allowed).
	
	Example 1:
	
	Input: nums = 
	[
	  [9,9,4],
	  [6,6,8],
	  [2,1,1]
	] 
	Output: 4 
	Explanation: The longest increasing path is [1, 2, 6, 9].
	Example 2:
	
	Input: nums = 
	[
	  [3,4,5],
	  [3,2,6],
	  [2,2,1]
	] 
	Output: 4 
	Explanation: The longest increasing path is [3, 4, 5, 6]. Moving diagonally(对角线) is not allowed.
*/
public class Longest_Increasing_Path_in_a_Matrix {

	public static int n = 10; // matrix.length; //行数

	public static int m = 10; // matrix[0].length; //列数

	public static int matrix[][] = new int[n][m];
			
//			  ={{-1,2 ,2 ,0, 3 ,3, 2, 0, 1, 1},
//			{0, 3 ,-1 ,0 ,-1 ,0 ,3 ,1, 1 ,2 },
//			{0 ,1, 0 ,2 ,-2 ,1, 2 ,-2, 1, 1 },
//			{1 ,-1 ,2 ,2 ,1 ,-2 ,0 ,1 ,0 ,3 },
//			{3 ,2 ,2, -1 ,3, 2, 0, 3, 1, 3 },
//			{0 ,0 ,3 ,2 ,2 ,-1 ,1 ,0 ,1 ,2 },
//			{2 ,0 ,-1 ,1 ,2 ,3 ,0, 3, 2, 0 },
//			{3 ,3 ,-2 ,-2 ,0 ,1 ,2, 1, 1 ,2 },
//			{0 ,0 ,3 ,0, 0, 0 ,-2 ,-1 ,3, 1 },
//			{2 ,0, 3 ,1, 1 ,-1 ,-1, 0 ,2 ,-2 }};

	public static int length = 0;

	public static int result[] = new int[n * m];

	/**
	 * 
	 * @param x
	 *            当前横坐标
	 * @param y
	 *            当前纵坐标
	 * @return 当前位置的递增最长路径长度
	 */
	public static int dfs(int x, int y, int[][] res) {
		
		Boolean hasnext = false;
		result[length] = matrix[x][y];
		
		//// 记忆化搜索
		if (res[x][y] != 0) {
			//没有下个更大的数时打印
//			if(res[x][y]==1){
//				 System.out.print("x="+x+", y="+y+" ");
//				 for (int k = 0; k <=length; k++) {
//					System.out.print(result[k]);
//				 }
//				 System.out.println();
//			}
			return res[x][y];
		}
		
	
		int max = 1;
	
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				// 保证是上下左右四个方向
				if (Math.abs(i + j) == 1) {
					// 检查坐标有效性
					if (x + i > -1 && x + i < n && y + j > -1 && y + j < m) {
						if (matrix[x][y] < matrix[x + i][y + j]) {
							result[length] = matrix[x][y];
							length++;
							hasnext=true;
							// 注意不要写成 max = Math.max(max, dfs(x + i, y + j,
							// res,list))+1;
							// 要把加1后的结果进行比较而不能比较后再加1
							// 递归式:maxpath(matrix[x][y]) = 1+ maxpath(matrix[x +
							// i][y + j])
							max = Math.max(max, 1 + dfs(x + i, y + j, res));
							length--;
						} 
 
					}   

				}
			}

		}
		
		if(!hasnext){
			 //没有下个更大的数时打印
			 System.out.print("x="+x+", y="+y+" ");
			 for (int k = 0; k <=length; k++) {
				System.out.print(result[k]);
			 }
			 System.out.println();
		}

		// 记忆化搜索
		res[x][y] = max;

		// 四个方向都找不到下一步时的递归分支返回max(不一定等于1,只有一步的层次max返回1)
		// 其余的每次递归max都加了1
		return max;

	}

	public static void main(String[] args) {
		for (int i = 0; i < n; i++) {
			//matrix[i] = TestUtil.generateRandomArray2(m, 3);
			for (int j = 0; j < m; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			 System.out.println();

		}

		int[][] res = new int[n][m];
		int max = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				max = Math.max(max, dfs(i, j, res));
				//	max = Math.max(max, dfs(2, 4, res));
			    Arrays.fill(result, 0);
//				每次的记忆化搜索主要为下一次的循环搜索服务
				for (int k = 0; k < n; k++) {
					for (int l = 0; l < m; l++) {
						System.out.print(res[k][l] + " ");
					}
					System.out.println();
				}
				System.out.println();
			}
			
		}
		System.out.println(max);

	}

}
