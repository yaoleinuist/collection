package com.lzhsite.leetcode.algoritom.practise.dynamicProgramming;

/*
	Given an unsorted array of integers, find the length of longest increasing subsequence.
	Example:
	Input: [10,9,2,5,3,7,101,18]
	Output: 4 
	Explanation: The longest increasing subsequence is [2,3,7,101], therefore the length is 4
 */
public class Longest_Increasing_Subsequence {

	public static int[] result = new int[1000];
	public static int length = 1;

	public int lengthOfLIS(int[] nums) {
		int max = 0;
		int[] rember=new int[nums.length];
		for (int i = 0; i < nums.length; i++) {
			result[0]=nums[i];
			max = Math.max(max, dfs(i, nums,rember));
		}
		return max;
	}

	/**
	 * 
	 * @param index  当前数据下标(每层递归都不断递增)
	 * @param nums   输入数组
	 * @param rember (记忆当前数据下标为起点的所有连续子序列里的数),每个数的最大连续子序列长度
	 * @return
	 */
	private int dfs(int index, int[] nums,int[] rember) {
		// TODO Auto-generated method stub
//		Boolean hasnext =false;
//		for (int i = index + 1; i < nums.length; i++) {
//			if (nums[index] < nums[i]) {
//				hasnext=true;
//			}
//		}
//		if(!hasnext){
//			for (int j = 0; j< length; j++) {
//				System.out.print(result[j] + " ");
//			}
//			System.out.println();
//		}
		
        if(rember[index]!=0){
        	return rember[index];
        }
        
        
		if (index == nums.length) {
			return 0;
		}


		int max = 1;

		for (int i = index + 1; i < nums.length; i++) {

			if (nums[index] < nums[i]) {
				result[length]=nums[i];
				length++;
				max = Math.max(max, 1 + dfs(i, nums,rember));
				length--;
			}

		}
		rember[index]=max;

		return max;
	}

	/**
	 * 利用非递归和二分查找把时间复杂度降为log(n)
	 * @param nums
	 */
	public int  nonRecursive(int[] nums){
		
		//dp[i]代表以nums[i]结尾的最长递增子序列的长度
		int[] dp =new int[nums.length];
		//ends[r]存储最大递增子序列长度为r+1序列的最小结尾值
		int[] ends =new int[nums.length];
		//他们之间的关系：如果num[i]=ends[r] 那么dp[i] = r
		
		ends[0]=nums[0];
		dp[0]=1;
		int l=0,m=0,r=0;
		
		
		for (int i = 1; i < nums.length; i++) {
			//nums[i]是否要放在ends的末尾
			Boolean isNext = false;
			while (l <= r) {
				m = (int)(l+r)/2;
				if(l==r){
					//ends里的已有数找不到比nums[i]小的数所以要放在最后
					 if(ends[m] <= nums[i]){
						 isNext =true;
					 }
					break;
				}
				
               if(ends[m] <= nums[i]){
            	   l = m + 1;
               }else{
            	   r = m - 1;
               }
	              
			}
			if(isNext){
				//ends里的已有数找不到比nums[i]小的数所以要放在最后
				ends[r+1] = nums[i];
				r = r+1;
			}else{
				//ends里的已有数找到比nums[i]小的数所以要替换原来的值
				ends[r] = nums[i];
			}
	 
			dp[i] = r+1;
		}
		int max =-10000;
		for (int i = 0; i < nums.length; i++) {
			//System.out.print(dp[i]+" ");
			max =Math.max(max, dp[i]);
		}
		
		return max;
	}
	
	public static void main(String[] args) {

		int[] muns = { 10, 9, 2, 5, 3, 7, 101, 18 };

		Longest_Increasing_Subsequence tIncreasing_Subsequence = new Longest_Increasing_Subsequence();
		//System.out.println(tIncreasing_Subsequence.lengthOfLIS(muns));
		 tIncreasing_Subsequence.nonRecursive(muns);
	}

}
