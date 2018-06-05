package com.lzhsite.technology.grammar;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

//如果只是获取key，或者value，推荐使用keySet或者values方式
//如果同时需要key和value推荐使用entrySet
//如果需要在遍历过程中删除元素推荐使用Iterator
//如果需要在遍历过程中增加元素，可以新建一个临时map存放新增的元素，等遍历完毕，再把临时map放到原来的map中

public class TestHashMap {

	public static void main(String[] args) {

		Map<String, String> map1 = new IdentityHashMap<>();
		map1.put("x", "y");
		map1.put("a", "b");
		map1.put("c", "d");
		map1.put("e", "d");
		map1.put("f", "b");
		map1.put("m", "n1");
		map1.put("m", "n2");
		for (Entry<String, String> entry : map1.entrySet()) {
			// System.out.println(entry.getKey()+" "+entry.getValue());
		}
		///////////////////////////////////////////////////////////////////
		Iterator<Map.Entry<String, String>> iter = null;
		Set<Map.Entry<String, String>> allSet = map1.entrySet();
		iter = allSet.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> me = iter.next();
			System.out.println(me.getKey() + " --> " + me.getValue());
		}

		for (Map.Entry<String, String> entry : allSet) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
		///////////////////////////////////////////////////////////////////
		//Iterator也有其优势：在用foreach遍历map时，如果改变其大小，会报错，但如果只是删除元素，
		//可以使用Iterator的remove方法删除元素
	    Iterator<Map.Entry<String, String>> it = map1.entrySet().iterator();
	    while (it.hasNext()) {
	      Map.Entry<String, String> entry = it.next();
	      System.out.println(entry.getKey() + ":" + entry.getValue());
	      // it.remove(); 删除元素
	    }
        ///////////////////////////////////////////////////////////////////
		map1.forEach((key, value) -> {
			System.out.println(key + ":" + value);
		});
		///////////////////////////////////////////////////////////////////
		for (String key : map1.keySet()) {
			System.out.println(key);
		}
		// values 获取value
		for (String value : map1.values()) {
			System.out.println(value);
		}
		for (String key : map1.keySet()) {
			System.out.println(key + ":" + map1.get(key));
		}
		///////////////////////////////////////////////////////////////////
	}
}
