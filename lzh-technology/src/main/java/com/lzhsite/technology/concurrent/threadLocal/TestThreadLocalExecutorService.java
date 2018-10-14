package com.lzhsite.technology.concurrent.threadLocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * ThreadLocal可以简单理解为key为当前Thread的一个Map，所以它和线程池一起使用的时候就会出现问题了，
 * 因为我们知道线程池有一个好处就是会让线程重用，避免创建过多的线程对象。也就是说有可能会出现ThreadLocal中的线程对象
 
 * 
 * 第1次循环刚开始，ThreadLocal中的值为：null
 * 第1次循环结束，ThreadLocal中的值为：1---
 * 当前线程名称为：pool-1-thread-1
 * ------------------
 * 第2次循环刚开始，ThreadLocal中的值为：1---
 * 第2次循环结束，ThreadLocal中的值为：2---
 * 当前线程名称为：pool-1-thread-1
 * ------------------
 * 第3次循环刚开始，ThreadLocal中的值为：2---
 * 第3次循环结束，ThreadLocal中的值为：3---
 * 当前线程名称为：pool-1-thread-1
 
 * 只要在使用ThreadLocal之前或者之后remove一下就好了。
 * @author lzhcode
 *
 */
public class TestThreadLocalExecutorService {
	static ExecutorService defaultFixedExecutor = Executors.newFixedThreadPool(1);
	static ThreadLocal<String> threadLocal = new ThreadLocal<String>();
 
	public static void main(String[] args) {
		for (int i = 1; i < 4; i++) {
			final int count = i;
			defaultFixedExecutor.submit(new Runnable() {
 
				@Override
				public void run() {
					System.out.println("第"+count+"次循环刚开始，ThreadLocal中的值为："+threadLocal.get());
					threadLocal.set(count+"---");
					System.out.println("第"+count+"次循环结束，ThreadLocal中的值为："+threadLocal.get());
					System.out.println("当前线程名称为："+Thread.currentThread().getName());
					System.out.println("------------------");
				}
			});
		
		}
	}
 
}
