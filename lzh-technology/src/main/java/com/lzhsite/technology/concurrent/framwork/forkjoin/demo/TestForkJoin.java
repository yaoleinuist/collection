package com.lzhsite.technology.concurrent.framwork.forkjoin.demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;

import org.junit.Test;

import lombok.Data;

public class TestForkJoin {
	@Test
	public void testForkJoinFramework() {
	    ForkJoinPool forkJoinPool = new ForkJoinPoolFactory().getObject();

	    Context context = new Context();
	    DefaultForkJoinDataLoader<Context> loader = new DefaultForkJoinDataLoader<>(context);
	    loader.addTask(new IDataLoader<Context>() {
	        @Override
	        public void load(Context context) {
	            context.addAns = 100;
	            System.out.println("add thread: " + Thread.currentThread());
	        }
	    });
	    loader.addTask(new IDataLoader<Context>() {
	        @Override
	        public void load(Context context) {
	            try {
	                Thread.sleep(3000);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            context.mulAns = 50;
	            System.out.println("mul thread: " + Thread.currentThread());
	        }
	    });
	    loader.addTask(new IDataLoader<Context>() {
	        @Override
	        public void load(Context context) {
	            context.concatAns = "hell world";
	            System.out.println("concat thread: " + Thread.currentThread());
	        }
	    });


	    DefaultForkJoinDataLoader<Context> subTask = new DefaultForkJoinDataLoader<>(context);
	    subTask.addTask(new IDataLoader<Context>() {
	        @Override
	        public void load(Context context) {
	            System.out.println("sub thread1: " + Thread.currentThread() + " | now: " + System.currentTimeMillis());
	            try {
	                Thread.sleep(200);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            context.ans.put(Thread.currentThread().getName(), System.currentTimeMillis());

	        }
	    });
	    subTask.addTask(new IDataLoader<Context>() {
	        @Override
	        public void load(Context context) {
	            System.out.println("sub thread2: " + Thread.currentThread() + " | now: " + System.currentTimeMillis());
	            context.ans.put(Thread.currentThread().getName(), System.currentTimeMillis());
	        }
	    });

	    loader.addTask(subTask);


	    long start = System.currentTimeMillis();
	    System.out.println("------- start: " + start);

	    // 提交任务，同步阻塞调用方式
	    forkJoinPool.invoke(loader);


	    System.out.println("------- end: " + (System.currentTimeMillis() - start));

	    // 输出返回结果，要求3s后输出，所有的结果都设置完毕
	    System.out.println("the ans: " + context);
	}
}

@Data 
class Context {
   public int addAns;

   public int mulAns;

   public String concatAns;

   public Map<String, Object> ans = new ConcurrentHashMap<>();
}
