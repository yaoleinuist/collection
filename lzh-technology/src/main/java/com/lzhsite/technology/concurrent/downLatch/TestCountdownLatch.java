package com.lzhsite.technology.concurrent.downLatch;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
/*
 使用countdownlatch工具 
当pendingFileVisits （原子性线程安全的变量）中访问的文件个数为零时，通知主线程结束
结果：23秒 提速50% 比单线程

Total Size: 50315772180
Time taken: 23.029064337*/
public class TestCountdownLatch {
	
	private ExecutorService service;
	final private AtomicLong pendingFileVisits = new AtomicLong();
	final private AtomicLong totalSize = new AtomicLong();
	final private CountDownLatch latch = new CountDownLatch(1);

	private void updateTotalSizeOfFilesInDir(final File file) {
		long fileSize = 0;
		if (file.isFile())
			fileSize = file.length();
		else {
			final File[] children = file.listFiles();
			if (children != null) {
				for (final File child : children) {
					if (child.isFile())
						fileSize += child.length();
					else {
						pendingFileVisits.incrementAndGet();
						service.execute(new Runnable() {
							public void run() {
								updateTotalSizeOfFilesInDir(child);
							}
						});
					}
				}
			}
		}
		//System.out.println(pendingFileVisits.get());
		totalSize.addAndGet(fileSize);// 共享变量 此处产生多个线程compete 堵塞
		if (pendingFileVisits.decrementAndGet() == 0)
			latch.countDown();
	}

	private long getTotalSizeOfFile(final String fileName) throws InterruptedException {
		service = Executors.newFixedThreadPool(100);
		pendingFileVisits.incrementAndGet();
		try {
			updateTotalSizeOfFilesInDir(new File(fileName));
			latch.await(100, TimeUnit.SECONDS);
			return totalSize.longValue();
		} finally {
			service.shutdown();
		}
	}

	public static void main(final String[] args) throws InterruptedException {
		final long start = System.nanoTime();
		final long total = new TestCountdownLatch().getTotalSizeOfFile("C:\\Users");
		final long end = System.nanoTime();
		System.out.println("Total Size: " + total);
		System.out.println("Time taken: " + (end - start) / 1.0e9);
	}
}
