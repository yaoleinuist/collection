package com.lzhsite.leetcode.algoritom.practise;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class 背包问题拓展 {

	private static int n, m, k;
	// 小美每种可乐的具体瓶数
	private static int[] xiaomei_kelenum;
	// 小团每种可乐的具体瓶数
	private static int[] xiaotuan_kelenum;

	private static int[] xiaomei_happynum;
	private static int[] xiaotuan_happynum;

	private static double max_happynum = 0;

	private static List<List<Integer>> result = new ArrayList<>();

	/**
	 * 
	 * @param xiaomei_num
	 *            小美剩余可乐数
	 * @param xiaotuan_num
	 *            小团剩余可乐数
	 * @param index
	 *            递归深度
	 */
	private static void dfs(int xiaomei_num, int xiaotuan_num, int index) {

		if (index == k) {
			xiaomei_kelenum[k - 1] = xiaomei_num;
			xiaotuan_kelenum[k - 1] = xiaotuan_num;
		}

		if (xiaomei_num == 0 && xiaotuan_num == 0 || index == k) {
			double sum = 0;
			for (int i = 0; i < k; i++) {
				
				
				sum = add(sum,  div(xiaomei_happynum[i] * (xiaomei_kelenum[i] + xiaotuan_kelenum[i]),n,2));
				sum = add(sum,  div(xiaotuan_happynum[i] * (xiaomei_kelenum[i] + xiaotuan_kelenum[i]), n,2));
			}
			if (Double.doubleToLongBits(sum)  >Double.doubleToLongBits(max_happynum)) {
				max_happynum = sum;
				result.clear();
				List list = new ArrayList<>();
				for (int i = 0; i < k; i++) {
					list.add(xiaomei_kelenum[i] + xiaotuan_kelenum[i]);
				}
				result.add(list);
			} else if (Double.doubleToLongBits(sum) == Double.doubleToLongBits(max_happynum)) {
				List list = new ArrayList<>();
				for (int i = 0; i < k; i++) {
					list.add(xiaomei_kelenum[i] + xiaotuan_kelenum[i]);
				}
				result.add(list);
			}
			return;
		}

		for (int i = 0; i < xiaomei_num + 1; i++) {
			for (int j = 0; j < xiaotuan_num + 1; j++) {
				xiaomei_kelenum[index] = i;
				xiaotuan_kelenum[index] = j;
				dfs(xiaomei_num - i, xiaotuan_num - j, index + 1);
			}
		}

	}

	private static List getDictMinList() {

		Collections.sort(result, new Comparator<List<Integer>>() {
			@Override
			public int compare(List<Integer> o1, List<Integer> o2) {

				for (int i = 0; i < o1.size(); i++) {
					Integer temp1 = o1.get(i);
					Integer temp2 = o2.get(i);
					if (temp1 > temp2) {
						// 返回1代表升序
						return 1;
					} else if (temp1 < temp2) {
						// 返回-1代表降序
						return -1;
					}
				}
				return 0;
			}
		});

		return result.get(0);
	}

	/**
	 * 提供精确加法计算的add方法
	 * 
	 * @param value1
	 *            被加数
	 * @param value2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double value1, double value2) {
		BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
		BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的除法运算方法div
	 * 
	 * @param value1
	 *            被除数
	 * @param value2
	 *            除数
	 * @param scale
	 *            精确范围
	 * @return 两个参数的商
	 * @throws IllegalAccessException
	 */
	public static double div(double value1, double value2, int scale){
		// 如果精确范围小于0，抛出异常信息
		if (scale < 0) {
			try {
				throw new IllegalAccessException("精确度不能小于0");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
		BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
		return b1.divide(b2, scale).doubleValue();
	}

	public static void main(String[] args) {

		Scanner input = new Scanner(System.in);
		n = input.nextInt();
		m = input.nextInt();
		k = input.nextInt();

		xiaomei_kelenum = new int[k];
		xiaotuan_kelenum = new int[k];
		xiaomei_happynum = new int[k];
		xiaotuan_happynum = new int[k];

		for (int i = 0; i < k; i++) {
			xiaomei_happynum[i] = input.nextInt();
			xiaotuan_happynum[i] = input.nextInt();
		}

		dfs(m, n - m, 0);

		for (int i = 0; i < result.size(); i++) {
			List<Integer> list = result.get(i);
		}
		List list = getDictMinList();
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i) + " ");
		}
	}
}

//题目描述
//小美和小团最近沉迷可乐。可供TA们选择的可乐共有k种，比如可口可乐、零度可乐等等，每种可乐会带给小美和小团不同的快乐程度。
//TA们一共要买n瓶可乐，每种可乐可以买无限多瓶，小美会随机挑选其中的m瓶喝，剩下的n-m瓶小团喝。
//请问应该如何购买可乐，使得小美和小团得到的快乐程度的和的期望值最大？
//现在请求出购买可乐的方案。
//输入描述:
//第一行三个整数n，m，k分别表示要买的可乐数、小美喝的可乐数以及可供选择的可乐种数。
//接下来k行，每行两个整数a，b分别表示某种可乐分别给予小美和小团的快乐程度。
//对于所有数据，1 <= n <= 10,000, 0 <= m <= n, 1 <= k <= 10,000, -10,000 <= a, b <= 10,000
//输出描述:
//一行k个整数，第i个整数表示购买第i种可乐的数目。
//如果有多解，请输出字典序最小的那个。
//对于两个序列 a1, a2, ..., ak, b1, b2, ..., bk，a的字典序小于b，当且仅当存在一个位置i <= k满足：
//ai < bi且对于所有的位置 j < i，aj = bj；
//示例1
//输入
//2 1 2
//1 2
//3 1
//输出
//0 2
//说明
//一共有三种购买方案：
//1. 买2瓶第一类可乐，小美和小团各喝一瓶，期望得到的快乐程度和为1+2=3；
//2. 买1瓶第一类可乐和1瓶第二类可乐，小美和小团各有二分之一的概率喝到第一类可乐，另有二分之一的概率喝到第二类可乐，期望得到的快乐程度和为1*0.5+3*0.5+2*0.5+1*0.5=3.5；
//3. 买2瓶第二类可乐，小美和小团各喝一瓶，期望得到的快乐程度和为3+1=4。
