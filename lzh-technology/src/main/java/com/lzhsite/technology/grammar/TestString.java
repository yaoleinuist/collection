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
	 * jdk1.6和1.7中intern的用法 
	 * http://blog.csdn.net/seu_calvin/article/details/52291082
	 */
	@Test
	public void test3() {
		/**
		 *  String s = new String("1")，生成了常量池中的“1” 和堆空间中的字符串对象。
		 *  s.intern()，这一行的作用是s对象去常量池中寻找后发现"1"已经存在于常量池中了。
		 *	String s2 = "1"，这行代码是生成一个s2的引用指向常量池中的“1”对象。
		 *	结果就是 s 和 s2 的引用地址明显不同。因此返回了false。
			
		 *	s3.intern()，这一行代码，是将 s3中的“11”字符串放入 String 常量池中，此时常量池中不存在“11”字符串，
		 *  JDK1.6的做法是直接在常量池中生成一个 "11" 的对象。
		 *	但是在JDK1.7中，常量池中不需要再存储一份对象了，
		 *  可以直接存储堆中的引用。这份引用直接指向 s3 引用的对象，也就是说s3.intern() ==s3会返回true。
		 *	String s4 = "11"， 这一行代码会直接去常量池中创建，
		 *  但是发现已经有这个对象了，此时也就是指向 s3 引用对象的一个引用。因此s3 == s4返回了true。
		 */
		String s = new String("1");  
		//s.intern();  
		String s2 = "1";  
		System.out.println(s == s2);  
		  
		String s3 = new String("1") + new String("1");  
		s3.intern();  
		String s4 = "11";  
		System.out.println(s3 == s4);  

		/**
		 * str1.intern() == str1就是上面例子中的情况，str1.intern()发现常量池中不存在“SEUCalvin”，
		 * 因此指向了str1。 "SEUCalvin"在常量池中创建时，也就直接指向了str1了。
		 * 两个都返回true就理所当然啦。
		 */
		String str1 = new String("SEU") + new String("Calvin");        
		System.out.println(str1.intern() == str1);     
		System.out.println(str1 == "SEUCalvin");  
	}
	
	@Test
	public void test4() {
 
		/**
		 * str2先在常量池中创建了“SEUCalvin”，那么str1.intern()当然就直接指向了str2，
		 * 你可以去验证它们两个是返回的true。后面的"SEUCalvin"也一样指向str2。
		 * 所以谁都不搭理在堆空间中的str1了，所以都返回了false。
		 */
		String str2 = "SEUCalvin";//新加的一行代码，其余不变  
		String str1 = new String("SEU")+ new String("Calvin");      
		System.out.println(str1.intern() == str1);   
		System.out.println(str1 == "SEUCalvin");   
 
	}
	
	@Test
	public void test5(){
		String a="abc";
		String b="abc";
		String c=new String("abc");
		String d="ab"+"c";
		
		
		System.out.println((a==b)+" "+(b==c)+" "+(a==d));
	}
	
}
