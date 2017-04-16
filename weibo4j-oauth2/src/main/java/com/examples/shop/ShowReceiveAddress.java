package com.examples.shop;

import java.util.Comparator;
import java.util.TreeMap;

import com.weibo4j.util.SignUtil;

public class ShowReceiveAddress {

	public static void main(String[] args) {
		// String access_token = args[0];
		// String uid = args[1];

		TreeMap<String, String> treeMap = new TreeMap<String, String>(new Comparator<String>() {

			public int compare(String o1, String o2) {
				// 指定排序器按照升序排列
				return o1.compareTo(o2);
			}
		});
		treeMap.put("start", "20141212");
		treeMap.put("end", "20141214");
		treeMap.put("page", "2");

		SignUtil.getSign(treeMap, "j7g5fjyrewqd54g76jj");
	}
}
