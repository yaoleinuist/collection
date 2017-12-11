package com.lzhsite.technology.grammar;

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
}
