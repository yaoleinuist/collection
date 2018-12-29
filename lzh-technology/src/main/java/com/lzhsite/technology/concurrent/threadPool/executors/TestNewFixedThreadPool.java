package com.lzhsite.technology.concurrent.threadPool.executors;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
//http://shoren.iteye.com/blog/1716690
//public static ExecutorService newFixedThreadPool(int nThreads)
//创建一个可重用固定线程数的线程池，以共享的无界队列方式来运行这些线程。在任意点，在大多数 nThreads 线程会处于处理任务的活动状态。
//如果在所有线程处于活动状态时提交附加任务，则在有可用线程之前，
//附加任务将在队列中等待。如果在关闭前的执行期间由于失败而导致任何线程终止，那么一个新线程将代替它执行后续的任务（如果需要）。
//在某个线程被显式地关闭之前，池中的线程将一直存在。
public class TestNewFixedThreadPool {
	 public static void main(String args[]) {  
	        Random random = new Random();  
	        //产生一个 ExecutorService 对象，这个对象带有一个大小为 poolSize 
	        //的线程池，若任务数量大于 poolSize ，任务会被放在一个 queue 里顺序执行。   
	        
	        //notice:这个线程池的队列LinkedBlockingQueue没有指定默认大小,高并发环境下
	        //这个线程池的队列LinkedBlockingQueue没有指定默认大小,高并发环境下
	        // 在对内存压力很大,所以生产环境一般都不使用这个,所以生产环境一般都不使用这个
	        ExecutorService executor = Executors.newFixedThreadPool(3);  
	        // 判断可是线程池可以结束  
	        int waitTime = 500;  
	        for (int i = 0; i < 10; i++) {  
	          String name = "线程 " + i;  
	          int time = random.nextInt(1000);  
	          waitTime += time;  
	          Runnable runner = new ExecutorThread(name, time);  
	          System.out.println("增加: " + name + " / " + time);  
	          executor.execute(runner);  
	        }  
	        try {  
	          Thread.sleep(waitTime);  
	          executor.shutdown();  
	          executor.awaitTermination(waitTime, TimeUnit.MILLISECONDS);  
	        } catch (InterruptedException ignored) {  
	        }  
	    }  
	}  
	  
	  
	class ExecutorThread implements Runnable {  
	    private final String name;  
	    private final int delay;  
	    public ExecutorThread(String name, int delay) {  
	        this.name = name;  
	        this.delay = delay;  
	    }  
	    public void run() {  
	        System.out.println("启动: " + name);  
	        try {  
	          Thread.sleep(delay);  
	        } catch (InterruptedException ignored) {  
	        }  
	        System.out.println("完成: " + name);  
	    }  
}

