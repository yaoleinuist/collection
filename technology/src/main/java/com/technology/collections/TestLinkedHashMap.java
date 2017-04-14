package com.technology.collections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
/*
Hashtable与 HashMap类似,它继承自Dictionary类，不同的是:它不允许记录的键或者值为空;它支持线程的同步，
即任一时刻只有一个线程能写Hashtable,因此也导致了 Hashtable在写入时会比较慢。

LinkedHashMap 是HashMap的一个子类，保存了记录的插入顺序，在用Iterator遍历LinkedHashMap时，
先得到的记录肯定是先插入的.也可以在构造时用带参数，按照应用次数排序。在遍历的时候会比HashMap慢，
不过有种情况例外，当HashMap容量很大，实际数据较少时，遍历起来可能会比 LinkedHashMap慢，
因为LinkedHashMap的遍历速度只和实际数据有关，和容量无关，而HashMap的遍历速度和他的容量有关。

TreeMap实现SortMap接口，能够把它保存的记录根据键排序,默认是按键值的升序排序，也可以指定排序的比较器，
当用Iterator 遍历TreeMap时，得到的记录是排过序的。
*/
public class TestLinkedHashMap {
	public static void main(String args[])  {
	   System.out.println("*************************LinkedHashMap*************");
	   Map<Integer,String> map = new LinkedHashMap<Integer,String>();
	   map.put(6, "apple");
	   map.put(3, "banana");
	   map.put(2,"pear");
	   
	   for (Iterator it =  map.keySet().iterator();it.hasNext();)
	   {
	    Object key = it.next();
	    System.out.println( key+"="+ map.get(key));
	   }
	   
	   System.out.println("*************************HashMap*************");
	   Map<Integer,String> map1 = new  HashMap<Integer,String>();
	   map1.put(6, "apple");
	   map1.put(3, "banana");
	   map1.put(2,"pear");
	   
	   for (Iterator it =  map1.keySet().iterator();it.hasNext();)
	   {
	    Object key = it.next();
	    System.out.println( key+"="+ map1.get(key));
	   }
	  }
}
