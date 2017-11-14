package com.lzhsite.technology.thread.blockQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
1)add(anObject):把anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则报异常
2)offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则返回false.
3)put(anObject):把anObject加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断直到BlockingQueue里面有空间再继续.
4)poll(time):取走BlockingQueue里排在首位的对象,若不能立即取出,则可以等time参数规定的时间,取不到时返回null
5)take():取走BlockingQueue里排在首位的对象,若BlockingQueue为空,阻断进入等待状态直到Blocking有新的对象被加入为止
 */

public class TestBlockingQueue2 {
	 public static void main(String[] args) {
	        ExecutorService service = Executors.newSingleThreadExecutor();
	        final Business3 business = new Business3();
	        service.execute(new Runnable(){

	            public void run() {
	                for(int i=0;i<50;i++){
	                    business.sub();
	                }
	            }
	            
	        });
	        
	        for(int i=0;i<50;i++){
	            business.main();
	        }
	    }

	}

	class Business3{
	    BlockingQueue subQueue = new ArrayBlockingQueue(1);
	    BlockingQueue mainQueue = new ArrayBlockingQueue(1);
	    //这里是匿名构造方法，只要new一个对象都会调用这个匿名构造方法，它与静态块不同，静态块只会执行一次，
	    //在类第一次加载到JVM的时候执行
	    //这里主要是让main线程首先put一个，就有东西可以取，如果不加这个匿名构造方法put一个的话程序就死锁了
	    {
	        try {
	            mainQueue.put(1);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }
	    public void sub(){
	        try
	        {
	            mainQueue.take();
	            for(int i=0;i<10;i++){
	                System.out.println(Thread.currentThread().getName() + " : " + i);
	            }
	            subQueue.put(1);
	        }catch(Exception e){

	        }
	    }
	    
	    public void main(){
	        
	        try
	        {
	            subQueue.take();
	            for(int i=0;i<5;i++){
	                System.out.println(Thread.currentThread().getName() + " : " + i);
	            }
	  
	            mainQueue.put(1);
	            
	        }catch(Exception e){
	        }        
	    }

}
 
