package com.lzhsite.leetcode.algoritom.practise.dynamicProgramming;

/**
 * 烧气球 给你一串气球，每个气球上都标了一个数字，这些数字用一个数组nums来表示。 如果你扎破第i个气球，你就可以获得 nums[left] *
 * nums[i] * nums[right] 个硬币，
 * 其中left和right是与第i个气球相邻的两个气球，当i被扎破后，left和right就变成直接相邻了。
 * 找出一个扎破所有气球的顺序，使得最后获得的硬币数量的总和最大。
 * 
 * 思路： dp[i][j]表示打爆区间[i,j]中的所有气球能得到的最多金币 假设[i...j]中k是最后一个破裂的，那么， [i, k-1] and
 * [k+1, j] 就是互相独立的了。 dp[i,j]=Math.max(max,dp[i][k-1]+dp[k+1][j]+ nums[i-1] *
 * nums[k] * nums[j+1]) (k=i~j,max=0)
 * 
 * @author lzhcode
 *
 */
public class Burst_Balloons {

	private int[] nums = new int[13];

	/**
	 * 超时的解法
	 * @return
	 */
	public int maxNumCoin() {

		int n = nums.length;
		int dp[][] = new int[n][n];
		dp[0][0] = nums[0] * nums[1];
		dp[0][1] = nums[0] * nums[1] * nums[2];

		int max = 0;
		//第一层是区间长度
		//第二层是左区间
		//第三层是k的位置
		for (int length = 1; length <= n; length++) {
			for (int left = 0; left < dp.length-length; left++) {
				int right= left + length-1;
				for (int k = left; k <= right; k++) {
					max = Math.max(max, dp[left][k - 1] + dp[k + 1][right] + nums[left - 1] * nums[k] * nums[right + 1]);
					dp[left][right] = max;
				}

			}
		}

		return dp[0][n - 1];
	}
	
	/**
	 * 用记忆化搜索优化
	 * @return
	 */
	public int maxCoins2() {
  
        int n = nums.length;
        int[][] cache = new int[n][n];
        return helper(nums, cache, 0, n - 1);
    }
    
    private int helper(int[] nums, int[][] cache, int left, int right) {
        if (left + 1 >= right) {
            return 0;
        }
        else if (cache[left][right] != 0) {
            return cache[left][right];
        }
        
        int ret = 0;
        for (int i = left; i <=right; i++) {
            ret = Math.max(ret, nums[left] * nums[i] * nums[right] + helper(nums, cache, left, i) + helper(nums, cache, i, right));
        }
        cache[left][right] = ret;
        return ret;
    }

 

}
