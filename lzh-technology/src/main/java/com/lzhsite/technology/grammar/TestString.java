package com.lzhsite.technology.grammar;

import org.junit.Test;

public class TestString {

	@Test
	public void test1() {

		/*
		 * regionMatches（boolean ignoreCase，int toffset，String other，int
		 * ooffset，int len）； regionMatches（int toffset，String other，int
		 * ooffset，int len）； 上述两个方法用来比较两个字符串中指定区域的子串。入口参数中，
		 * 用toffset和ooffset分别指出当前字符串中的子串起始位置和要与之比较的字符串中的子串起始地址； len
		 * 指出比较长度。前一种方法可区分大写字母和小写字母，如果在 boolean ignoreCase处写 true，表示将不区分大小写，
		 * 写false则表示将区分大小写。而后一个方法认为大小写字母有区别。由此可见，实际上前一个方法隐含了后一个方法的功能
		 * 
		 */

		String s1 = "tsinghua";
		String s2 = "it is TsingHua";
		s1.regionMatches(0, s2, 6, 7);
	}

	@Test
	public void test2() {
		Integer youNumber = 2;
		// 0 代表前面补充0
		// 4 代表长度为4
		// d 代表参数为正数型
		String str = String.format("%02d", youNumber);
		System.out.println(str); // 0001

	}

	/**
	 * jdk1.6和1.7中intern的用法 j Jdk6
	 * 以及以前的版本中，字符串的常量池是放在堆的Perm区的，Perm区是一个类静态的区域(方法区)，主要存储一些加载类的信息，常量池，方法片段等内容，默认大小只有4m
	 * jdk7 主要对 intern 操作和常量池做了以下改动 1.将String常量池从Perm区移动到了Java Heap区
	 * 2.String#intern方法时，如果存在堆中的对象，会直接保存对象的引用，而不会重新创建对象。
	 * http://blog.csdn.net/seu_calvin/article/details/52291082
	 */
	@Test
	public void test3() {
		/**
		 * String s = new String("1")，生成了常量池中的"1" 和堆空间中的字符串对象。
		 * s.intern()，这一行的作用是s对象去常量池中寻找后发现"1"已经存在于常量池中了,返回常量池里的字符串对象 String s2 =
		 * "1"，这行代码是生成一个s2的引用指向常量池中的"1"对象。 结果就是 s 和 s2 的引用地址明显不同。因此返回了false。
		 * 
		 * s3.intern()，这一行代码，是将 s3中的"11"字符串放入 String 常量池中，此时常量池中不存在“11”字符串，
		 * JDK1.6的做法是直接在常量池中生成一个 "11" 的对象。 但是在JDK1.7中，常量池中不需要再存储一份对象了，
		 * 可以直接存储堆中的引用。这份引用直接指向 s3 引用的对象，也就是说s3.intern() ==s3会返回true。 String s4
		 * = "11"， 这一行代码会直接去常量池中创建， 但是发现已经有这个对象了，此时也就是指向 s3 引用对象的一个引用。因此s3 ==
		 * s4返回了true。
		 */
		String s = new String("1");
		s.intern();
		String s2 = "1";
		System.out.println("s == s2 ? " + (s == s2));
		System.out.println("s.intern() == s2 ? " + (s.intern() == s2));

		String s3 = new String("1") + new String("1");
		s3.intern();
		String s4 = "11";
		System.out.println("s3 == s4 ? " + (s3 == s4));

	    
		String str1 = new String("SEU") + new String("Calvin");
		System.out.println(str1.intern() == str1);
		System.out.println(str1 == "SEUCalvin");
	}

	@Test
	public void test4() {


		//str2指向常量池,堆中没有SEUCalvin对象
		String str2 = "SEUCalvin"; 
		//str1指向堆中新的对象SEUCalvin,常量池里有SEU,Calvin,SEUCalvin
		String str1 = new String("SEU") + new String("Calvin");
		System.out.println(str1.intern() == str1);
		System.out.println(str1 == "SEUCalvin");

	}

	/**
	 * 直接使用纯字符串串联来创建String对象，则仅仅会检
	 * 查维护String池中的字符串，池中没有就在池中创建一个，有则罢了！
	 * 但绝不会在堆栈区再去创建该String对 象；
	 */
	@Test
	public void test5() {
		//a指向常量池,堆中没有abc对象
		String a = "abc";
		//b指向常量池,堆中没有abc对象
		String b = "abc";
		//c指向堆中新的对象
		String c = new String("abc");
		//d指向常量池
		String d = "ab" + "c";
        // false false false
		System.out.println((a == b) + " " + (b == c) + " " + (a == d));
	}

	/**
	 * 使用在编译期间可以确定结果的变量表达式来创建String对象，则仅仅会检 查维护String池中的字符串，池中没有就在池中创建一个，有则罢了！
	 * 但绝不会在堆栈区再去创建该String对 象；
	 * 
	 * 
	 */
	@Test
	public void test6() {
		//a指向常量池
		String a = "xiaomeng2";
		final String b = "xiaomeng";
		String d = "xiaomeng";
		// c = b + 2在编译期间就可以确定,指向常量池
		String c = b + 2;
		//e指向堆中新的对象
		String e = d + 2;
		System.out.println((a == c));
		System.out.println((a == e));
	}

	@Test
	public void test7() {
		// 在池中和堆中分别创建String对象"abc",s1指向堆中对象
		String s1 = new String("abc");
		// s2直接指向池中对象"abc"
		String s2 = "abc";
		// 在堆中新创建"abc"对象，s3指向该对象
		String s3 = new String("abc");
		// 在池中创建对象"ab" 和 "c"，并且s4指向池中对象"abc"
		String s4 = "ab" + "c";
		// c指向池中对象"c"
		String c = "c";
		// 在堆中创建新的对象"abc"，并且s5指向该对象
		String s5 = "ab" + c;

		// 在堆中创建新的对象"abc"，并且s6指向该对象
		String s6 = "ab".concat("c");
		// 在堆中创建新的对象"abc"，并且s7指向该对象
		String s7 = "ab".concat(c);

		System.out.println("------------实串-----------");
		System.out.println(s1 == s2); // false
		System.out.println(s1 == s3); // false
		System.out.println(s2 == s3); // false
		System.out.println(s2 == s4); // true
		System.out.println(s2 == s5); // false
		System.out.println(s2 == s6); // false
		System.out.println(s2 == s7); // false
		System.out.println(s6 == s3); // false
		System.out.println(s7 == s3); // false
	}

}
