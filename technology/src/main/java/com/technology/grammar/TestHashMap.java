package com.technology.grammar;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

 

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
            //System.out.println(entry.getKey()+" "+entry.getValue());
        }
    
        Iterator<Map.Entry<String,String>> iter = null ; 
        Set<Map.Entry<String,String>>  allSet = map1.entrySet() ; 
        iter = allSet.iterator() ; 
        while(iter.hasNext()){ 
            Map.Entry<String,String> me = iter.next() ; 
            System.out.println(me.getKey() + " --> " + me.getValue()) ; 
        } 
	}
}
