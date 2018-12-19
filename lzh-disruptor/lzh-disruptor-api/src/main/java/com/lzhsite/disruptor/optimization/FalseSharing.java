package com.lzhsite.disruptor.optimization;

import java.util.concurrent.CountDownLatch;

public class FalseSharing implements Runnable {
	// 线程个数
	public static int NUM_THREADS = 4; // change
	// 循环修改数组中数据的次数
	public final static long ITERATIONS = 500L * 1000L * 1000L;
	// 数组下标
	private final int arrayIndex;
	// 操作的数组
	private static VolatileLong[] longs;

	private static final CountDownLatch cdl = new CountDownLatch(NUM_THREADS);

	public FalseSharing(final int arrayIndex) {
		this.arrayIndex = arrayIndex;
	}

	public static void main(final String[] args) throws Exception {
		Thread.sleep(10000);
		System.out.println("starting....");
		// 初始化数组
		longs = new VolatileLong[NUM_THREADS];
		for (int i = 0; i < longs.length; i++) {
			longs[i] = new VolatileLong();
		}
		final long start = System.nanoTime();
		runTest();
		System.out.println("duration = " + (System.nanoTime() - start));
	}

	private static void runTest() throws InterruptedException {
		// 初始化线程组
		Thread[] threads = new Thread[NUM_THREADS];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new FalseSharing(i));
		}
		// 开始运行所有线程
		for (Thread t : threads) {
			t.start();
		}
		// 主线程阻塞直到所有子线程结束
		cdl.await();
	}

	@Override
	public void run() {
		// 多线程情况下持续修改数组中某一个volatile值
		long i = ITERATIONS + 1;
		while (0 != --i) {
			longs[arrayIndex].value = i;
		}
		cdl.countDown();
	}
}
//使用缓存行填充，耗时(ns):
//65286845549 (65.2s)
//去掉缓存行填充，耗时(ns):
//112066911990  (112.0s)
