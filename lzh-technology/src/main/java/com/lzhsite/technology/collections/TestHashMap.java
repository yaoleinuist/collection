package com.lzhsite.technology.collections;

import java.util.HashMap;

public class TestHashMap {

	public static void main(String[] args) {

		HashMap map = new HashMap<>();

		map.put("1", 1);
		map.put("2", 2);
		map.put("3", 3);
		map.put("4", 4);
		map.put("1", 1);
		map.put("2", 2);
		map.put("3", 3);
		map.put("4", 4);

		java.util.Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
			System.out.println(entry.getValue());
		}

	}
}
