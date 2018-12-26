package com.lzhsite.technology.grammar;

import java.lang.reflect.Field;

import org.elasticsearch.monitor.os.OsStats.Swap;
import org.junit.Test;

public class TestInteger {

	/**
	 * 为什么Java中1000==1000为false而100==100为true？
	 * 查看Integer.java类，会发现有一个内部私有类，IntegerCache.java，它缓存了从-128到127之间的所有的整数对象。
	 * 所以例子中i1和i2指向了一个对象。因此100==100为true。
	 */
	@Test
	public void test1() {

		Integer i1 = 100, i2 = 100;
		System.out.println(i1 == i2);
		Integer i3 = 1000, i4 = 1000;
		System.out.println(i3 == i4);

	}

	/**
	 * 编译后的代码
	 *  Integer a = Integer.valueOf(1);
     *  Integer b = Integer.valueOf(2);
	 *  swap(a, b);
	 *  System.out.println("a=" + a + ",b=" + b);
	 *  Integer c = Integer.valueOf(1); Integer d = Integer.valueOf(2);
	 *  System.out.println("c=" + c + "; d=" + d);
	 *  
	 *  在该区间(1~128)内所有的Integer.valueOf(int)函数返回的对象，是根据int值计算的偏移量，
	 *  从数组Integer.IntegerCache.cache中获取，对象是同一个，不会新建对象。
     *  所以当我们修改了Integer.valueOf(1)的value后，所有Integer.IntegerCache.cache[ 1 ~ 128 ]
     *  的返回值都会变更。
	 */
	@Test
	public void test2() {
		Integer a = 1;
		Integer b = 2;
		swap(a, b);
		System.out.println("a=" + a + ",b=" + b);

		Integer c = 1, d = 2;
		System.out.println("c=" + c + "; d=" + d);

	}

	private void swap(Integer a, Integer b) {
 
		try {
			Field field = Integer.class.getDeclaredField("value");
			field.setAccessible(true);
			int temp = a.intValue();
			// 注意这里要用setInt不能用set传递否则Object自动对int类型数进行装箱会导致错误
		    // public void set(Object obj, Object value)
			// public void setInt(Object obj, int i)
			field.setInt(a, b.intValue());
			field.setInt(b, temp);

		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
