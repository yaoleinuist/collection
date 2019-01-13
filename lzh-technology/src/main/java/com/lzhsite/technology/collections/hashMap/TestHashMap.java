package com.lzhsite.technology.collections.hashMap;

import java.util.HashMap;
/**
 * 
	             HashMap         Hashtable
	父类                   AbstractMap     Dictiionary
	是否同步            否                           是
	k，v可否null  是                           否
	
	 
	HashMap是Hashtable的轻量级实现（非线程安全的实现），他们都完成了Map接口，
	
	主要区别在于HashMap允许空（null）键值（key）,由于非线程安全，效率上可能高于Hashtable。
	 
	HashMap允许将null作为一个entry的key或者value，而Hashtable不允许。
	 
	HashMap把Hashtable的contains方法去掉了，改成containsvalue和containsKey。因为contains方法容易让人引起误解。 
	 
	Hashtable继承自Dictionary类，而HashMap是Java1.2引进的Map interface的一个实现。
	 
	最大的不同是，Hashtable的方法是Synchronize的，而HashMap不是，在多个线程访问Hashtable时，不需要自己为它的方法实现同步，
	而HashMap 就必须为之提供外同步(Collections.synchronizedMap)。 
	
	
	Hashtable,synchronized是针对整张Hash表的，即每次锁住整张表让线程独占，
 * @author lzhcode
 *
 */
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
