package com.lzhsite.leetcode.algoritom.practise;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class List去重 {
	/**
	 * 效率最低
	 * @param list
	 */
	public static List removeDuplicate(List list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).equals(list.get(i))) {
					//remove涉及到数据的拷贝，比较耗时
					list.remove(j);
				}
			}
		}
		return list;
	}

	/**
	 *  效率次高
	 * @param list
	 */
	private static void removeDuplicate2(List<String> list) {
		LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
		set.addAll(list);
		list.clear();
		list.addAll(set);
	}

	/**
	 * 效率次低
	 * @param list
	 */
	private static void removeDuplicate3(List<String> list) {
		List<String> result = new ArrayList<String>(list.size());
		for (String str : list) {
			//contains涉及到字符串的遍历
			if (!result.contains(str)) {
				result.add(str);
			}
		}
		list.clear();
		list.addAll(result);
	}
	/**
	 * 效率最高
	 * @param list
	 */
	private static void removeDuplicate4(List<String> list) {
		HashSet<String> set = new HashSet<String>(list.size());
		List<String> result = new ArrayList<String>(list.size());
		for (String str : list) {
			if (set.add(str)) {
				result.add(str);
			}
		}
		list.clear();
		list.addAll(result);
	}

	public static void main(String[] args) {

		final List<String> list = new ArrayList<String>();
		for (int i = 0; i < 1000; i++) {
			list.add("haha-" + i);
		}

		long time = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			removeDuplicate(list);
		}
		long time1 = System.currentTimeMillis();
		System.out.println("time1:" + (time1 - time));

		for (int i = 0; i < 10000; i++) {
			removeDuplicate2(list);
		}
		long time2 = System.currentTimeMillis();
		System.out.println("time2:" + (time2 - time1));

		for (int i = 0; i < 1000; i++) {
			removeDuplicate3(list);
		}
		long time3 = System.currentTimeMillis();
		System.out.println("time3:" + (time3 - time2));

		for (int i = 0; i < 1000; i++) {
			removeDuplicate4(list);
		}
		long time4 = System.currentTimeMillis();
		System.out.println("time4:" + (time4 - time3));
	}

}
