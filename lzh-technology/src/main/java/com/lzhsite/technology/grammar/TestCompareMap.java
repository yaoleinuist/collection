package com.lzhsite.technology.grammar;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.lzhsite.dto.PersonDTO;

/**
 * 
 * @author lzhcode
 *
 */
public class TestCompareMap {

	/**
	 *  两个HashMap里面的内容是否相等
	 */
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
		Boolean b = true;

		if (map1.values().size() != map2.values().size()) {
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
	/**
	 *  map深复制
	 */
	@Test
	public void test2() {
		// https://www.cnblogs.com/cxxjohnson/p/6258742.html
		// https://blog.csdn.net/lzkkevin/article/details/6667958
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("key1", 1);

		Map<String, Integer> mapFirst = new HashMap<String, Integer>();
		mapFirst.putAll(map); // 只对基本数据类型进行深拷贝，改成objet就出错了

		System.out.println(mapFirst);

		map.put("key2", 2);

		System.out.println(mapFirst);
		// 如上，输出结果为：
		// {key1=1}
		// {key1=1}
		//有一种方法，是使用序列化的方式来实现对象的深拷贝，但是前提是，对象必须是实现了Serializable接口才可以，
		//Map本身没有实现 Serializable 这个接口，所以这种方式不能序列化Map，也就是不能深拷贝Map。但是HashMap是可以的，
		//因为它实现了 Serializable。下面的方式，基于HashMap来讲，非Map的拷贝。

		//具体实现如下：com.lzhsite.core.utils.CloneUtils 
	}
	
	
	/**
	 * lombok底层重写了对象的hashcode和equal导致对象的所有属性都一样是就认为这两个对象相等
	 * 否者每个新new的对象都可视为不同的key
	 *
	 */
	@Test
	public void test3() throws Exception {
		
		Map<PersonDTO,String> map=new HashMap<>();
        for (int i = 0; i <5; i++) {
        	PersonDTO personDTO = new PersonDTO();
    		personDTO.setAge(12);
    		personDTO.setName("test");
    		map.put(personDTO,i+"");
		}
	
      System.out.println(map.size());
 

	}
	
}
