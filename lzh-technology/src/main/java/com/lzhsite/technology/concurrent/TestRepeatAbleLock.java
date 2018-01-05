package com.lzhsite.technology.concurrent;
/**
 * 测试可重入锁
 * @author Administrator
 *
 */
public class TestRepeatAbleLock  implements Runnable{
 
		public synchronized void get(){
			System.out.println(Thread.currentThread().getId());
			set();
		}

		public synchronized void set(){
			System.out.println(Thread.currentThread().getId());
		}

		@Override
		public void run() {
			get();
		}
		public static void main(String[] args) {
			TestRepeatAbleLock ss=new TestRepeatAbleLock();
			new Thread(ss).start();
			new Thread(ss).start();
			new Thread(ss).start();
		}
 


}
