package com.lzhsite.technology.concurrent.waitNotify;
/*
	 wait()和notify()的通常用法
	
	Java多线程开发中，我们常用到wait()和notify()方法来实现线程间的协作，简单的说步骤如下： 
	1. A线程取得锁，执行wait()，释放锁; 
	2. B线程取得锁，完成业务后执行notify()，再释放锁; 
	3. B线程释放锁之后，A线程取得锁，继续执行wait()之后的代码；
	 
	
	启动线程A，取得锁之后先启动线程B再执行wait()方法，释放锁并等待；
	线程B启动之后会等待锁，A线程执行wait()之后，线程B取得锁，然后启动线程C，再执行notify唤醒线程A，最后退出synchronize代码块，释放锁;
	线程C启动之后就一直在等待锁，这时候线程B还没有退出synchronize代码块，锁还在线程B手里；
	线程A在线程B执行notify()之后就一直在等待锁，这时候线程B还没有退出synchronize代码块，锁还在线程B手里；
	线程B退出synchronize代码块，释放锁之后，线程A和线程C竞争锁；
	把上面的代码在Openjdk8下面执行，反复执行多次，都得到以下结果：
	
	thread-A : get lock
	thread-A : start wait
	thread-B : get lock
	thread-C : c thread is start
	thread-B : start notify
	thread-B : release lock
	thread-A : after wait, acquire lock again
	thread-A : release lock
	thread-C : get lock
	thread-C : release lock
	 
	针对以上结果，问题来了： 
	第一个问题： 
	将以上代码反复执行多次，结果都是B释放锁之后A会先得到锁，这又是为什么呢？C为何不能先拿到锁呢？
	
	第二个问题： 
	线程C自开始就执行了monitorenter指令，它能得到锁是容易理解的，但是线程A呢？在wait()之后并没有没有monitorenter指令，那么它又是如何取得锁的呢？
	 
	原文：https://blog.csdn.net/boling_cavalry/article/details/77793224 
*/ 
public class NotifyDemo {
	
	private static void sleep(long sleepVal) {
		try {
			Thread.sleep(sleepVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void log(String desc) {
		System.out.println(Thread.currentThread().getName() + " : " + desc);
	}

	Object lock = new Object();

	public void startThreadA() {
		new Thread(() -> {
			synchronized (lock) {
				log("get lock");
				startThreadB();
				log("start wait");
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				log("get lock after wait");
				log("release lock");
			}
		}, "thread-A").start();
	}

	public void startThreadB() {
		new Thread(() -> {
			synchronized (lock) {
				log("get lock");
				startThreadC();
				sleep(100);
				log("start notify");
				lock.notify();
				log("release lock");

			}
		}, "thread-B").start();
	}

	public void startThreadC() {
		new Thread(() -> {
			synchronized (lock) {
				log("get lock");
				log("release lock");
			}
		}, "thread-C").start();
	}

	public static void main(String[] args) {
		new NotifyDemo().startThreadA();
	}

}
