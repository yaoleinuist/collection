package com.lzhsite.technology.collections.hashMap;

import java.util.HashMap;

public class TestHashMap {

	public static void main(String[] args) {

		HashMap<String,Integer> map = new HashMap<>();

		map.put("1", 1);
		map.put("2", 2);
		map.put("3", 3);
		map.put("4", 4);
	 

		HashMap<String,Integer> map2 = new HashMap<>();

		map2.put("1", 4);
		map2.put("2", 3);
		map2.put("3", 2);
		map2.put("4", 1);

		//后面put进来的会覆盖之前的
		map2.putAll(map);
	 
		for (String key : map2.keySet()) {
			System.out.println(key + ":" + map2.get(key));
		}
		
		

	}
}
