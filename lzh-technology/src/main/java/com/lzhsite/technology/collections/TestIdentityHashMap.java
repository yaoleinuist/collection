package com.lzhsite.technology.collections;

import java.util.IdentityHashMap;
import java.util.Map.Entry;
/**
 * 在java中，有一种key值可以重复的map，就是IdentityHashMap。在IdentityHashMap中，
 * 判断两个键值k1和 k2相等的条件是 k1 == k2 。在正常的Map 实现（如 HashMap）中，
 * 当且仅当满足下列条件时才认为两个键 k1 和 k2 相等：(k1==null ? k2==null : e1.equals(e2))。
 * IdentityHashMap类利用哈希表实现 Map 接口，比较键（和值）时使用引用相等性代替对象相等性。
 * 该类不是 通用 Map 实现！此类实现 Map 接口时，它有意违反 Map 的常规协定，该协定在比较对象时强制
 * 使用 equals 方法。此类设计仅用于其中需要引用相等性语义的罕见情况。
 * @author lzhcode
 *
 */
public class TestIdentityHashMap {

	
	public static void main(String[] args) {
	    IdentityHashMap<String,Object> map =new IdentityHashMap<String,Object>();  
	    map.put(new String("xx"),"first");  
	    map.put(new String("xx"),"second");  
	    for (Entry<String, Object> entry : map.entrySet()) {  
	        System.out.print(entry.getKey() +"    ");  
	        System.out.println(entry.getValue());  
	    }  
	    System.out.println("idenMap="+map.containsKey("xx"));  
	    System.out.println("idenMap="+map.get("xx"));  	
	}
}
