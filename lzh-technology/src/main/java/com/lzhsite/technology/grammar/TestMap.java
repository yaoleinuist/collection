package com.lzhsite.technology.grammar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
/**
 * 两个HashMap里面的内容是否相等
 * @author lzhcode
 *
 */
public class TestMap {
 
	@Test
	public void test1() {

		Map<String, Integer> map1 = new HashMap<String, Integer>();
		Map<String, Integer> map2 = new HashMap<String, Integer>();
		map1.put("1", 1);
		map1.put("2", 2);
		map1.put("3", 3);
 
		
		map2.put("1", 1);
		map2.put("2", 2);
		map2.put("3", 3);
		map2.put("4", 4);
		Boolean b=true;

		if(map1.values().size()!=map2.values().size()){
			b = false;
		}
		Iterator<Entry<String, Integer>> it1 = map1.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<String, Integer> entry1 = it1.next();
			Integer integer2 = map2.get(entry1.getKey());
			if (integer2 == null || (!integer2.equals(entry1.getValue()))) {
				b = false;
				break;
			}
		}
		System.out.println(b);

	}
	
	 
}
