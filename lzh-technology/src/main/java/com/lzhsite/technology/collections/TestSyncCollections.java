package com.lzhsite.technology.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TestSyncCollections {

	public static void main(String[] args) {
		/*
		 * java中有个集合工具类Collections，它可以将一个ArrayList编程线程安全的）。
		 * 
		 * 把arraylist转换成同步的
		 */
		List list = Collections.synchronizedList(new ArrayList());
		synchronized (list) {
			Iterator i = list.iterator(); // Must be in synchronized block
			while (i.hasNext())
				 i.next();
		}
	}
}
