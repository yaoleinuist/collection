package com.lzhsite.leetcode.algoritom.practise.deepFirst;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/*
	 全排列组合的递归规律：
	集合s的全排列组合 all(s)=n+all(s-n);其中n为已经取出的集合
	以集合 s={1,2,3}为例,则s的全排列组合为all(s)={1}+all({2,3});其中n={1},s-n={2,3}
	通过以上例子，我们可以知道上述算法可以用递归来解决。
	我们取极端情况，如果集合s为空，那么说明不需要再进行递归。
	全排列组合，如果集合有4个元素，则全排列组合的个数为 A(4,4)=4*3*2*1=24种，代码如下：
*/
public class FullPermutation {
	private int n;

	public FullPermutation() {
		this.n = 0;
	}

	/**
	 * 
	 * @param candidate
	 *            剩余可供选择的list
	 * @param prefix
	 *            已经选择的数组成的字符串
	 */
	public void listAll(List candidate, String prefix) {

		if (candidate.isEmpty()) {
			System.out.println(prefix);
			this.n++;
		}
		for (int i = 0; i < candidate.size(); i++) {
			List temp = new LinkedList(candidate);// 转换成linkList,移除一个对象是在不影响原来队列的基础上的
			String s1 = prefix + temp.remove(i);// 用于保存排列组合生成的结果
			listAll(temp, s1);// 注意，这里temp和s1都是全新的集合和字符串，并不是一直对一个集合来进行操作

		}

	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public static void main(String[] args) {
		String[] arr = { "1", "2", "3", "4" };
		FullPermutation f = new FullPermutation();
		f.listAll(Arrays.asList(arr), "");
		System.out.println("所有的排列个数：" + f.getN());
	}
}
