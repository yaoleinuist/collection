package com.lzhsite.technology.concurrent.threadPool.executors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//Java主线程等待所有子线程执行完毕
public class TestNewCachedThreadPool {
	
	  public static void main(String[] args) {  
		  
		    //SynchronousQueue只要有个一个线程处于阻塞,就不允许添加数据
		    //所以newCachedThreadPool实际项目一般也很少运用
		    ExecutorService threadPool = Executors.newCachedThreadPool();  
		    for (int i = 0; i < 10; i++) {  
		  
		      threadPool.execute(new Runnable() {  
		        public void run() {  
		  
		          try {  
		            Thread.sleep(1000);  
		            // 模拟子线程任务  
		          } catch (InterruptedException e) {  
		          }  
		          System.out.println("子线程" + Thread.currentThread() + "执行完毕");  
		  
		        }  
		      });  
		    }  
		  
		    // 启动一次顺序关闭，执行以前提交的任务，但不接受新任务。  
		    threadPool.shutdown();  
		  
		    try {  
		      // 请求关闭、发生超时或者当前线程中断，无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行  
		      // 设置最长等待10秒  
		      threadPool.awaitTermination(10, TimeUnit.SECONDS);  
		    } catch (InterruptedException e) {  
	  
		      e.printStackTrace();  
		    }  
		  
		    System.out.println("主线执行。");  
		  }  
}
