package com.lzhsite.leetcode.algoritom.practise.greed;

/**
 * 一个n位的数，去掉其中的k位，问怎样去掉使得留下来的那个（n-k）位的数最小？
 * 需要注意的是，给定的整数大小可以超过long类型的范围，所以需要用字符串来表示。
 * 
 * 
 * 思路： 现在假设有一个数，124682385， 假如k = 1，则结果为12462385,k = 2，结果为1242385……
 * 可以知道：最优解是删除出现的第一个左边>右边的数，因为删除之后高位减小，
 * 那全局最优解也就是这个了，因为删除S个数字就相当于执行了S次删除一个数，因为留下的数总是当前最优解
 * 
 * @author lzhcode
 *
 */
public class 删去k个数字后的最小值 {

	/**
	 * 删除整数的k个数字，获得删除后的最小值
	 * 
	 * @param num 原整数
	 * @param k   删除数量
	 * 
	 */
	public static String removeKDigits(String num, int k) {

		String numNew = num;

		for (int i = 0; i < k; i++) {
			boolean hasCut = false;
			// 从左向右遍历，找到比自己右侧数字大的数字并删除
			for (int j = 0; j < numNew.length() - 1; j++) {

				if (numNew.charAt(j) > numNew.charAt(j + 1)) {
					numNew = numNew.substring(0, j) + numNew.substring(j + 1, numNew.length());
					hasCut = true;
					break;
				}
			}

			// 如果没有找到要删除的数字，则删除最后一个数字
			if (!hasCut) {
				numNew = numNew.substring(0, numNew.length() - 1);
			}

			// 清除整数左侧的数字0
			numNew = removeZero(numNew);
		}

		// 如果整数的所有数字都被删除了，直接返回0
		if (numNew.length() == 0) {
			return "0";
		}
		return numNew;

	}

	private static String removeZero(String num) {

		for (int i = 0; i < num.length() - 1; i++) {
			if (num.charAt(0) != '0') {
				break;
			}
			num = num.substring(1, num.length());
		}

		return num;

	}

	/**
	 * removeKDigits的优化
	 * 1.每一次内层循环，都需要从头遍历所有数字
     * 2.subString  方法的底层实现，涉及到了新字符串的创建，以及逐个字符的拷贝。这个方法自身的时间复杂度是O（n）。
	 * 
	 * 下面代码中非常巧妙地运用了栈的特性，在遍历原整数的数字时，让所有数字一个个入栈，当某个数字需要删除时，
	 * 让该数字出栈。最后，程序把栈中的元素转化为字符串结果。
	 * 删除整数的k个数字，获得删除后的最小值
	 * @param num 原整数
	 * @param k   删除数量
	 */
	public 	static 	String removeKDigits2(String num, int k) {

		// 新整数的最终长度 = 原整数长度 - k
		int newLength = num.length() - k;
		// 创建一个栈，用于接收所有的数字
		char[] stack = new char[num.length()];
		int top = 0;

		for (int i = 0; i < num.length(); ++i) {

			// 遍历当前数字
			char c = num.charAt(i);
			// 当栈顶数字大于遍历到的当前数字，栈顶数字出栈（相当于删除数字）
			while (top > 0 && stack[top - 1] > c && k > 0) {
				top -= 1;
				k -= 1;
			}
			// 遍历到的当前数字入栈
			stack[top++] = c;
		}

		// 找到栈中第一个非零数字的位置，以此构建新的整数字符串
		int offset = 0;
		while (offset < newLength && stack[offset] == '0') {
			offset++;
		}

		return offset == newLength ? "0" : new
		String(stack, offset, newLength - offset);
	}

 

	public static void main(String[] args) {

		System.out.println(removeKDigits("1593212", 3));
		System.out.println(removeKDigits("30200", 1));
		System.out.println(removeKDigits("10", 2));
		System.out.println(removeKDigits("541270936", 3));
 
 

	}
}
