package com.lzhsite.technology.thread.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

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
