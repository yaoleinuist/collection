package com.lzhsite.technology.concurrent.interview.second;

import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;

public class Test {
    
	public static void main(String[] args) {
		final Semaphore semaphore = new Semaphore(1);
		/**
		 * https://www.cnblogs.com/duanxz/p/3252267.html
		 * SynchronousQueue是一个没有数据缓冲的BlockingQueue，生产者线程对其的插入操作put必须等待消费者的移
		 * 除操作take。在线程池里的一个典型应用是Executors.newCachedThreadPool()就使用了SynchronousQueue，
		 * 这个线程池根据需要（新任务到来时）创建新的线程，如果有空闲线程则会重复使用，线程空闲了60秒后会被回收。
		 */
		final SynchronousQueue<String> queue = new SynchronousQueue<String>();
		for(int i=0;i<10;i++){
			new Thread(new Runnable(){
				@Override
				public void run() {	
					try {
						semaphore.acquire();
						String input = queue.take();
						String output = TestDo.doSome(input);
						System.out.println(Thread.currentThread().getName()+ ":" + output);
						semaphore.release();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
			}).start();
		}
		
		System.out.println("begin:"+(System.currentTimeMillis()/1000));
		for(int i=0;i<10;i++){  //这行不能改动
			String input = i+"";  //这行不能改动
			try {
				queue.put(input);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

//不能改动此TestDo类
class TestDo {
	public static String doSome(String input){
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String output = input + ":"+ (System.currentTimeMillis() / 1000);
		return output;
	}
}
