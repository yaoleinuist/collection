package com.lzhsite.leetcode.algoritom.practise.dynamicProgramming;

/**
 * 给你一根长度为n的绳子，请把绳子剪成m段 (m和n都是整数，n>1并且m>1)
 * 每段绳子的长度记为nums[0],nums[1],…,nums[m].请问nums[0]nums[1]…*nums[m]可能的最大乘积是多少?
 * 例如，当绳子的长度为8时，我们把它剪成长度分别为2,3,3的三段，此时得到的最大乘积是18.
 */

public class 剪绳子 {



	/**
	 * 思路：dp[n][m] 代表长度为n的绳子，切成m段的最大乘积
     * dp[n][m] = max(dp[n-k][m-1]*k) k=[1~n/2]
	 * @param n
	 * @return
	 */
	public int maxNumAfterCutting(int n,int m) {
		
		int[][] dp = new int[n + 1][m + 1];
		for (int i = 1; i <= n; i++) {
			dp[i][1] = i;
		}

		//第一层切成的段数
		//第二层绳子的总长度
		//第三层是k

		for (int sengment = 2; sengment <= m; sengment++) {
			int max = 0;
			for (int length = sengment; length <=n; length++) {
				for (int k = 1; k <length/2; k++) {
					max = Math.max(max, dp[length - k][sengment - 1] * k);
					dp[length][sengment]=max;
				}
			}
	
		}

		return dp[n][m];

	}

	/**
	 * 思路：定义函数f(n)为长度为n的绳子剪成若干段后各段长度乘积的最大值。在剪第一刀的时候，
	 * 我们有n-1种可能的选择，也就是剪出来的第一段绳子的长度分别为1,2...n-1。
	 * 因此f(n)=max(f(i)*f(n-i))，其中0<i<n。这是一个从上至下的递归公式，递归会有很多重复的子问题。我们可以从下而上的顺序计算，
	 * 也就是说我们先得到f(2)，f(3)，再得到f(4)，f(5)，直到得到f(n)
	 */
	public int maxNumAfterCutting2(int n) {
		if (n < 2)
			return 0;
		// 绳子长度为2时，只能剪成1和1
		if (n == 2)
			return 1;
		// 只可能为长度为1和2的2段或者长度都为1的三段，最大值为2
		if (n == 3)
			return 2;
		// 当长度大于3时，长度为3的段的最大值时3
		int f[] = new int[n + 1];
		f[0] = 0;
		f[1] = 1;
		f[2] = 2;
		f[3] = 3;
		int max = 0;
		for (int i = 4; i <= n; i++) {
			max = 0;
			for (int j = 1; j <= i / 2; j++) {
				int sum = f[j] * f[i - j];
				if (sum > max) {
					max = sum;
					f[i] = max;
				}
			}
		}
		return f[n];
	}
	
	public static void main(String[] args) {
		剪绳子 t=new 剪绳子();
		System.out.println(t.maxNumAfterCutting(8,3));
	}
	
}
