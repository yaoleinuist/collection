package com.lzhsite.technology.concurrent.threadPool;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestThreadPoolExecutor3 {
	private static int queueDeep = 4;

	public void createThreadPool() {
		/*
		 * 1.IO密集型 估算线程池大小 线程数 = CPU可用核心数/(1-阻塞系数)
		 * 阻塞系数= IO时间/(IO时间+CPU时间)
		 * 计算密集型任务的阻塞系数为0，而IO密集型任务的阻塞系数则接近于1。一个完全阻塞的任务是注定要挂掉的，所以我们无须担心阻塞系数会达到1。
		 * 阻塞系数可以采用一些性能分析工具或java.lang.managenment
		 * API来确定线程话在系统I/O操作上的时间与CPU密集任务所消耗的时间比值。
		 *
		 * 2.计算密集型估算线程池大小threadNum = cpuNum +1 或则 计算密集型估算线程池大小threadNum = cpuNum
		 * 
		 * 自定义线程池，最小线程数为2，最大线程数为4，线程池维护线程的空闲时间为3秒，
		 * 使用队列深度为4的有界队列，如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，
		 * 然后重试执行程序（如果再次失败，则重复此过程），里面已经根据队列深度对任务加载进行了控制。
		 */
		ThreadPoolExecutor tpe = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(queueDeep), new ThreadPoolExecutor.DiscardOldestPolicy());

		// 向线程池中添加 10 个任务
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while (getQueueSize(tpe.getQueue()) >= queueDeep) {
				System.out.println("队列已满，等3秒再添加任务");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			TaskThreadPool ttp = new TaskThreadPool(i);
			System.out.println("put i:" + i);
			tpe.execute(ttp);
		}

		tpe.shutdown();
	}

	private synchronized int getQueueSize(Queue queue) {
		return queue.size();
	}

	public static void main(String[] args) {
		TestThreadPoolExecutor3 test = new TestThreadPoolExecutor3();
		test.createThreadPool();
	}

	class TaskThreadPool implements Runnable {
		private int index;

		public TaskThreadPool(int index) {
			this.index = index;
		}

		public void run() {
			System.out.println(Thread.currentThread() + " index:" + index);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
