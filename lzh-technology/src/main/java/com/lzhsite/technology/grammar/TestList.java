package com.lzhsite.technology.grammar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestList {
	public static void main(String[] args) {

		int size = 100;
		final List<String> list = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			list.add("haha-" + i);
		}
		Iterator<String> iterator = list.iterator();
//		调用list.remove()方法导致modCount和expectedModCount的值不一致。
//      注意，像使用for-each进行迭代实际上也会出现这种问题。

        while(iterator.hasNext()){
        	String s = iterator.next();
            if(s.equals("haha-" + 2))
                list.remove("haha-" + 2);
        }
 
	}
}
