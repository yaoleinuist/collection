package com.lzhsite.technology.concurrent.threadLocal;

import java.text.SimpleDateFormat;
import java.util.Locale;


//http://blog.csdn.net/zdp072/article/details/41044059
/*
 * 创建三个进程， 使用静态成员变量SimpleDateFormat的parse和format方法，
 * 然后比较经过这两个方法折腾后的值是否相等：
 * 程序如果出现以下错误，说明传入的日期就串掉了，即SimpleDateFormat不是线程安全的
 *  Exception in thread "Thread-0" java.lang.RuntimeException: parse failed
		at cn.test.DateFormatTest$1.run(DateFormatTest.java:27)
		at java.lang.Thread.run(Thread.java:662)
	Caused by: java.lang.RuntimeException: Thread-0, Expected 01-Jan-1999 but got 01-Jan-2000
		at cn.test.DateFormatTest$1.run(DateFormatTest.java:22)
		... 1 more
		
		
        解决方案 :使用ThreadLocal: 每个线程都将拥有自己的SimpleDateFormat对象副本。
 **/

public class TestDateFormat extends Thread {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
	private static String date[] = { "01-Jan-1999", "01-Jan-2000", "01-Jan-2001" };

	public static void main(String[] args) {
		for (int i = 0; i < date.length; i++) {
			final int temp = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						while (true) {

							String str1 = date[temp];

							//Date date = DateUtil.parse(str1);
							//String str2 = DateUtil.format(date);

							String str2 = sdf.format(sdf.parse(str1));
							System.out.println(Thread.currentThread().getName() + ", " + str1 + "," + str2);
							if (!str1.equals(str2)) {
								throw new RuntimeException(
										Thread.currentThread().getName() + ", Expected " + str1 + " but got " + str2);
							}
						}
					} catch (Exception e) {
						throw new RuntimeException("parse failed", e);
					}
				}
			}).start();
		}
	}
}
