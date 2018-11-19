package com.lzhsite.leetcode.algoritom.practise.subsets;
/**
 * 给定可能包含重复数的整数集合，返回所有可能的子集。
 * 注意事项：
 * 子集中的元素必须是非降序的。
 * 解决方案集必须不包含重复子集。
 * 例如，如果s=[1,2,2]，解决方案是：
 *	 
 *	[
 *	  [2],
 *	  [1],
 *	  [1,2,2],
 *	  [2,2],
 *	  [1,2],
 *	  []
 *	]
 * @author lzhcode
 *
 */

import java.util.Arrays;

public class SubsetsII {
 
	//标记数组对应下标的数据取或不取
	private  static Boolean[] visits =new Boolean[100];
	
	
	public static void reboot(int index,int nums[]){
		
		if(index==nums.length){
			for (int i = 0; i < nums.length; i++) {
				if(visits[i]==true){
					System.out.print(nums[i]);
				}
			}
			System.out.println();
			return;
		}
		
		//数据一样的时候保证只以前面的数取或不取为准(前面相同的数据不取的话后面一定不取)
		if(index>0 && nums[index-1]==nums[index] && visits[index-1]==false){
			visits[index]=false;
			reboot(index+1,nums);
		}else {
			//取数据
			visits[index]=true;
			reboot(index+1,nums);
			//不取数据
			visits[index]=false;
			reboot(index+1,nums);
		}

		
	}
	
	public static void main(String[] args) {
		int nums[]={1,2,0,1};
		Arrays.sort(nums);
		reboot(0,nums);
	}
}
