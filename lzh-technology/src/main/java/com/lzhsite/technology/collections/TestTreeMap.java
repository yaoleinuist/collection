package com.lzhsite.technology.collections;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Test;

/*
1、TreeMap如不指定排序器，默认将按照key值进行升序排序，如果指定了排序器，则按照指定的排序器进行排序。
2、具体的排序规则，开发人员可以在int compare()方法中进行指定。
*/
public class TestTreeMap {

	@Test
	public void test1() {

		// 不指定排序器
		TreeMap<String, String> treeMap1 = new TreeMap<String, String>();
		treeMap1.put("2", "1");
		treeMap1.put("b", "1");
		treeMap1.put("1", "1");
		treeMap1.put("a", "1");
		System.out.println("treeMap1=" + treeMap1);

		// 指定排序器
		TreeMap<String, String> treeMap2 = new TreeMap<String, String>(new Comparator<String>() {

			/*
			 * int compare(Object o1, Object o2) 返回一个基本类型的整型， 返回负数表示：o1 小于o2，
			 * 返回0 表示：o1和o2相等， 返回正数表示：o1大于o2。
			 */
			public int compare(String o1, String o2) {

				// 指定排序器按照降序排列
				return o2.compareTo(o1);
			}
		});
		treeMap2.put("2", "1");
		treeMap2.put("b", "1");
		treeMap2.put("1", "1");
		treeMap2.put("a", "1");
		System.out.println("treeMap2=" + treeMap2);
	}
 
	@Test
	public void test2() {
		Integer count =1000000;
		Random random =new Random();
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < count; i++) {
			map.put(i+"", i+"");
		}
		long time1 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			map.get(i+"");
		}
		long time2 = System.currentTimeMillis();
		System.out.println("HashMap time:" + (time2 - time1));
		////////////////////////////////////////////////////////////////////////
		Map<String, String> linkedMap = new LinkedHashMap<String, String>();
		for (int i = 0; i < count; i++) {
			linkedMap.put(i+"", i+"");
		}
 
		time1 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			 linkedMap.get(i+"");
		}
		time2 = System.currentTimeMillis();
		System.out.println("LinkedHashMap time:" + (time2 - time1));
        ////////////////////////////////////////////////////////////////////////
		Map<String, String> treeMap = new TreeMap<String, String>();
		for (int i = 0; i < count; i++) {
			treeMap.put(i+"", i+"");
		}

		time1 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			treeMap.get(i+"");
		}
		time2 = System.currentTimeMillis();
		System.out.println("TreeMap time:" +  (time2 - time1));
	}

}
