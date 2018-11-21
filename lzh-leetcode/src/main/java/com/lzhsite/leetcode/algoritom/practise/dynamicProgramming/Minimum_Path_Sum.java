package com.lzhsite.leetcode.algoritom.practise.dynamicProgramming;


/*Given a m x n grid filled with non-negative numbers, find a path from top left to bottom right which minimizes the sum of all numbers along its path.

Note: You can only move either down or right at any point in time.

Example:

Input:
[
  [1,3,1],
  [1,5,1],
  [4,2,1]
]
Output: 7
Explanation: Because the path 1→3→1→1→1 minimizes the sum.*/
public class Minimum_Path_Sum {

	public int minPathSum(int[][] grid) {

		int n = grid.length;
		int m = grid[0].length;
		int dp[][] = new int[n][m];

		dp[0][0] = grid[0][0];

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < m; j++) {
				if (i == 0 && j > 0) {
					dp[0][j] = dp[0][j - 1] + grid[0][j];
				}
				if (j == 0 && i > 0) {
					dp[i][0] = dp[i - 1][0] + grid[i][0];
				}
				if (i != 0 && j!= 0) {
					dp[i][j] = Math.min(dp[i][j - 1] , dp[i - 1][j]) + grid[i][j];
				}
				
 
			}
		}
		return dp[n - 1][m - 1];
	}

	public static void main(String[] args) {

		int[][] grid = { { 1, 3, 1 }, { 1, 5, 1 }, { 4, 2, 1 } };
 

		Minimum_Path_Sum minimum_Path_Sum = new Minimum_Path_Sum();
		System.out.println(minimum_Path_Sum.minPathSum(grid));
	}
}
