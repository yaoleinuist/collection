package com.lzhsite.technology.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.junit.Test;


/**
 * http://blog.csdn.net/javazejian/article/details/50896505
 * 
 * 通过这样的方式去创建线程的话，最大的好处就是能够返回结果，加入有这样的场景，我们现在需要计算一个数据，
 * 而这个数据的计算比较耗时，而我们后面的程序也要用到这个数据结果，那么这个时Callable岂不是最好的选择？
 * 我们可以开设一个线程去执行计算，而主线程继续做其他事，而后面需要使用到这个数据时，我们再使用Future获取不就可以
 * 了吗？下面我们就来编写一个这样的实例
 * @author lzhcode
 *
 */
public class TestFutureTask {

	@Test
	public void Test1(){
		
//      //创建线程池  
//      ExecutorService es = Executors.newSingleThreadExecutor();  
//      //创建Callable对象任务  
//      CallableDemo calTask=new CallableDemo();  
//      //提交任务并获取执行结果  
//      Future<Integer> future =es.submit(calTask);  
//      //关闭线程池  
//      es.shutdown();  
          
        //创建线程池  
        ExecutorService es = Executors.newSingleThreadExecutor();  
        //创建Callable对象任务  
        CallableDemo calTask=new CallableDemo();  
        //创建FutureTask  
        FutureTask<Integer> futureTask=new FutureTask<>(calTask);  
        //执行任务  
        es.submit(futureTask);  
        //关闭线程池  
        es.shutdown();  
        try {  
            Thread.sleep(2000);  
        System.out.println("主线程在执行其他任务");  
          
        if(futureTask.get()!=null){  
            //输出获取到的结果  
            System.out.println("futureTask.get()-->"+futureTask.get());  
        }else{  
            //输出获取到的结果  
            System.out.println("futureTask.get()未获取到结果");  
        }  
          
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        System.out.println("主线程在执行完成");  
    }  
	
	@Test
	public void Test2(){
		   //创建线程池  
        ExecutorService es = Executors.newSingleThreadExecutor();  
        //创建Callable对象任务  
        CallableDemo calTask=new CallableDemo();  
        //提交任务并获取执行结果  
        Future<Integer> future =es.submit(calTask);  
        //关闭线程池  
        es.shutdown();  
        try {  
            Thread.sleep(2000);  
        System.out.println("主线程在执行其他任务");  
          
        if(future.get()!=null){  
            //输出获取到的结果  
            System.out.println("future.get()-->"+future.get());  
        }else{  
            //输出获取到的结果  
            System.out.println("future.get()未获取到结果");  
        }  
          
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        System.out.println("主线程在执行完成");  
	}
 
}

class CallableDemo implements Callable<Integer> {  
    
    private int sum;  
    @Override  
    public Integer call() throws Exception {  
        System.out.println("Callable子线程开始计算啦！");  
        Thread.sleep(2000);  
          
        for(int i=0 ;i<5000;i++){  
            sum=sum+i;  
        }  
        System.out.println("Callable子线程计算结束！");  
        return sum;  
    }  
}  
