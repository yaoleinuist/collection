package com.lzhsite.leetcode.algoritom.practise.subsets;
/**
 * 给定不包含重复数的整数集合，返回所有可能的子集。
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Subsets {

	public int[] S = {};
	public int len = 0;
	public static List<List<Integer>> ans = new ArrayList<List<Integer>>();

	public List<List<Integer>> subsets(int[] S) {
		this.S = S;
		this.len = S.length;
		Arrays.sort(S);

		// length of subsets: from 0 to S.length
		//比如取from=0 tar=8
		//第一层递归先取第一个数剩下7个数,第二次循环先取第二个数剩下7个数...
		//第二层递归取第二个数剩下6个数,第二次循环先取第三个数剩下6个数...
		//...
		for (int i = 0; i <= len; i++) {
			backTracking(new ArrayList<Integer>(), 0, i);
		}
		return ans;
	}

	/**
	 * 
	 * @param list   已经选中的子集
	 * @param from   当前递归选中下标为form的数
	 * @param tar    子集元素总个数
	 */
	public void backTracking(List<Integer> list, int from, int tar) {
		if (list.size() == tar) {
			List<Integer> res = new ArrayList<Integer>(list);
			ans.add(res);
		} else {
			
			for (int i = from; i < len; i++) {
				list.add(S[i]);
				backTracking(list, i + 1, tar);
				list.remove(new Integer(S[i]));
			}
		}
	}

	public static void main(String[] args) {
		int nums[] = { 1, 2, 0, 5 };
		Arrays.sort(nums);
		Subsets subsets = new Subsets();
		subsets.subsets(nums);
		for (List<Integer> list : ans) {
			for (Integer x : list) {
				System.out.print(x);
			}
			System.out.println();
		}
	}
}
