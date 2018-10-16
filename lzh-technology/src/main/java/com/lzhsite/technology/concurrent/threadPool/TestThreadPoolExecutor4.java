package com.lzhsite.technology.concurrent.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
/*
 * 如何选择线程池数量
 * 线程池的大小决定着系统的性能，过大或者过小的线程池数量都无法发挥最优的系统性能。
 * 当然线程池的大小也不需要做的太过于精确，只需要避免过大和过小的情况。一般来说，确定线程池的大小需要考虑CPU的数量，内存大小，任务是计算密集型还是IO密集型等因素
 * NCPU = CPU的数量
 * UCPU = 期望对CPU的使用率 0 ≤ UCPU ≤ 1
 * W/C = 等待时间与计算时间的比率
 * 如果希望处理器达到理想的使用率，那么线程池的最优大小为：
 * 线程池大小=NCPU *UCPU(1+W/C)
 * 在Java中使用
 * int ncpus = Runtime.getRuntime().availableProcessors();
 * 获取CPU的数量。
 * */
public class TestThreadPoolExecutor4 {
	public class MyThread implements Runnable , Comparable<MyThread>{
		protected String name;
		public MyThread(){
		}
		public MyThread(String name){
			this.name=name;
		}
		@Override
		public void run() {
			try {
				Thread.sleep(100);
				System.out.println(name+" ");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		@Override
		public int compareTo(MyThread o) {
			int me=Integer.parseInt(this.name.split("_")[1]);
			int other=Integer.parseInt(o.name.split("_")[1]);
			if(me>other)return 1;
			else if(me<other)return -1;
			else
			return 0;
		}
	}
	
	@Test
	public void testThreadPoolExecutor1() throws InterruptedException {

		long starttime=System.currentTimeMillis();
		ExecutorService exe=new ThreadPoolExecutor(100,200,0L,TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(200));
		for(int i=0;i<1000;i++){
			exe.execute(new MyThread("testThreadPoolExecutor1_"+Integer.toString(i)));
		}
		System.out.println();
		long endtime=System.currentTimeMillis();
		System.out.println("testThreadPoolExecutor1"+": "+(endtime-starttime));
		System.out.println("testThreadPoolExecutor1 exe size"+": "+((ThreadPoolExecutor)exe).getPoolSize());
		Thread.sleep(1000*101);
	}
	
	@Test
	public void testThreadPoolExecutor2() throws InterruptedException {
		long starttime=System.currentTimeMillis();
		ExecutorService exe=new ThreadPoolExecutor(100,200,0L,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());
		for(int i=0;i<1000;i++){
			exe.execute(new MyThread("testThreadPoolExecutor2_"+Integer.toString(i)));
		}
		System.out.println();
		long endtime=System.currentTimeMillis();
		System.out.println("testThreadPoolExecutor2"+": "+(endtime-starttime));
		System.out.println("testThreadPoolExecutor2 exe size"+": "+((ThreadPoolExecutor)exe).getPoolSize());
		Thread.sleep(1000);
	}

	@Test
	public void testThreadPoolExecutor3() throws InterruptedException {

		long starttime=System.currentTimeMillis();
		ExecutorService exe=new ThreadPoolExecutor(100,200,0L,TimeUnit.SECONDS,new PriorityBlockingQueue<Runnable>());
		for(int i=0;i<1000;i++){
			exe.execute(new MyThread("testThreadPoolExecutor3_"+Integer.toString(999-i)));
		}
		System.out.println();
		long endtime=System.currentTimeMillis();
		System.out.println("testThreadPoolExecutor3"+": "+(endtime-starttime));
		System.out.println("testThreadPoolExecutor3 exe size"+": "+((ThreadPoolExecutor)exe).getPoolSize());
		Thread.sleep(1000);
	}
}
