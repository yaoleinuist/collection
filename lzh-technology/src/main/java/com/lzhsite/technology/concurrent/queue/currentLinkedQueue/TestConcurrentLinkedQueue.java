package com.lzhsite.technology.concurrent.queue.currentLinkedQueue;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

	/**
	 * 生产
	 */
	public static void offer() {
		for (int i = 0; i < 100000; i++) {
			queue.offer(i);
		}
	}

	/**
	 * 消费
	 * 
	 * @author 林计钦
	 * @version 1.0 2013-7-25 下午05:32:56
	 */
	static class Poll implements Runnable {
		public void run() {
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
结果居然相差那么大，看了下ConcurrentLinkedQueue的API原来.size()是要遍历一遍集合的，难怪那么慢，所以尽量要避免用size而改用isEmpty().
总结了下， 在单位缺乏性能测试下，对自己的编程要求更加要严格，特别是在生产环境下更是要小心谨慎
*/
