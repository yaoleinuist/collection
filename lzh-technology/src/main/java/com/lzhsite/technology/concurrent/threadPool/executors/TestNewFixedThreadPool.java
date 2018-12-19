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
	        //在大多数 nThreads 线程会处于处理任务的活动状态点,所以生产环境一般都不使用这个
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
/*
	Executors 类
	   需要注意的是 Executors 是一个类，不是 Executor 的复数形式。该类是一个辅助类，此包中所定义的 Executor、ExecutorService、ScheduledExecutorService、ThreadFactory 和 
	   Callable 类的工厂和实用方法。
	此类支持以下各种方法：
	• 创建并返回设置有常用配置字符串的 ExecutorService 的方法。
	• 创建并返回设置有常用配置字符串的 ScheduledExecutorService 的方法。
	• 创建并返回“包装的”ExecutorService 方法，它通过使特定于实现的方法不可访问来禁用重新配置。
	• 创建并返回 ThreadFactory 的方法，它可将新创建的线程设置为已知的状态。
	•创建并返回非闭包形式的 Callable 的方法，这样可将其用于需要 Callable 的执行方法中。
	 Executors 提供了以下一些 static 的方法：
	callable(Runnable task): 将 Runnable 的任务转化成 Callable 的任务
	newSingleThreadExecutor: 产生一个 ExecutorService 对象，这个对象只有一个线程可用来执行任务，若任务多于一个，任务将按先后顺序执行。
	newCachedThreadPool(): 产生一个 ExecutorService 对象，这个对象带有一个线程池，线程池的大小会根据需要调整，线程执行完任务后返回线程池，供执行下一次任务使用。
	newFixedThreadPool(int poolSize) ： 产生一个 ExecutorService 对象，这个对象带有一个大小为 poolSize 的线程池，若任务数量大于 poolSize ，任务会被放在一个 queue 里顺序执行。
	newSingleThreadScheduledExecutor ：产生一个 ScheduledExecutorService 对象，这个对象的线程池大小为 1 ，若任务多于一个，任务将按先后顺序执行。
	newScheduledThreadPool(int poolSize): 产生一个 ScheduledExecutorService 对象，这个对象的线程池大小为 poolSize ，若任务数量大于 poolSize ，
	任务会在一个 queue 里等待执行。
	*/
