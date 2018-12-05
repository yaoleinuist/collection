package com.lzhsite.technology.concurrent.queue.currentLinkedQueue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *  是一个适用于高并发场景下的队列，通过无锁(CAS)的方式，实现了高并发状态下的高性能，
 *  通常ConcurrentLikedQueue性能好于BlockingQueue。
 *  
 *  什么时CAS请看https://www.cnblogs.com/longshiyVip/p/5205689.html
 * 
 *  ConcurrentLinkedQueue的其他方法：
 *　    peek()：获取表头元素但不移除队列的头，如果队列为空则返回null。
 *　    remove(Object obj)：移除队列已存在的元素，返回true，如果元素不存在，返回false。
 *　    add(E e)：将指定元素插入队列末尾，成功返回true，失败返回false（此方法非线程安全的方法，不推荐使用）。
 *  注意：
 *　   虽然ConcurrentLinkedQueue的性能很好，但是在调用size()方法的时候，会遍历一遍集合，对性能损害较大，执行很慢，最好用isEmpty()方法。
 *　   ConcurrentLinkedQueue不允许插入null元素，会抛出空指针异常。
 *　   ConcurrentLinkedQueue是无界的，所以使用时，一定要注意内存溢出的问题。即对并发不是很大中等的情况下使用，不然占用内存过多或者溢出，对程序的性能影响很大
 *  
 *  通过以上这些说明，不使用锁而单纯的使用CAS操作要求在应用层面上保证线程安全，并处理一些可能存在的不一致问题，大大增加了程序的设计和实现的难度。但是它带来的好处就是可以得到性能的飞速提升。
 * 
 * 下面代码是测试size()和isEmpty()性能的代码
 * 
 * @author lzhcode
 *
 */
public class TestConcurrentLinkedQueue {
	
	private static ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<Integer>();
	private static int count = 2; // 线程个数
	// CountDownLatch，一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待。
	private static CountDownLatch latch = new CountDownLatch(count);

	public static void main(String[] args) throws InterruptedException {
		long timeStart = System.currentTimeMillis();
		ExecutorService es = Executors.newFixedThreadPool(4);
		TestConcurrentLinkedQueue.offer();
		for (int i = 0; i < count; i++) {
			es.submit(new Poll());
		}
		latch.await(); // 使得主线程(main)阻塞直到latch.countDown()为零才继续执行
		System.out.println("cost time " + (System.currentTimeMillis() - timeStart) + "ms");
		es.shutdown();
	}
 
	public static void offer() {
		for (int i = 0; i < 100000; i++) {
			queue.offer(i);
		}
	}

 
	static class Poll implements Runnable {
		public void run() {
			//主要测试这个两个方法
			// while (queue.size()>0) {
			while (!queue.isEmpty()) {
				System.out.println(queue.poll());
			}
			latch.countDown();
		}
	}
}

/*
       运行结果：
	costtime 2360ms
	改用while (queue.size()>0)后
	运行结果：
	cost time 46422ms
*/
