package com.lzhsite.technology.concurrent.threadPool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//如果并发的线程数量很多，并且每个线程都是执行一个时间很短的任务就结束了，
//这样频繁创建线程就会大大降低系统的效率，因为频繁创建线程和销毁线程需要时间。
//
//那么有没有一种办法使得线程可以复用，就是执行完一个任务，并不被销毁，而是可以继续执行其他的任务
//
//在Java中可以通过线程池来达到这样的效果
public class TestThreadPoolExecutor {
	  
    private ThreadPoolExecutor pool = null;  
      
      
    /** 
     * 线程池初始化方法 
     *  
     * corePoolSize 核心线程池大小----10 
     * maximumPoolSize 最大线程池大小----30 
     * keepAliveTime 线程池中超过corePoolSize数目的空闲线程最大存活时间----30+单位TimeUnit 
     * TimeUnit keepAliveTime时间单位----TimeUnit.MINUTES 
     * workQueue 阻塞队列----new ArrayBlockingQueue<Runnable>(10)====10容量的阻塞队列 
     * threadFactory 新建线程工厂----new CustomThreadFactory()====定制的线程工厂 
     * rejectedExecutionHandler 当提交任务数超过maxmumPoolSize+workQueue之和时, 
     *                          即当提交第41个任务时(前面线程都没有执行完,此测试方法中用sleep(100)), 
     *                                任务会交给RejectedExecutionHandler来处理 
     */  
    public void init() {  
        pool = new ThreadPoolExecutor(  
                10,  
                30,  
                30,  
                TimeUnit.MINUTES,  
                new ArrayBlockingQueue<Runnable>(10),  
                new CustomThreadFactory(),  
                new CustomRejectedExecutionHandler());  
    }  
  
      
    public void destory() {  
        if(pool != null) {  
            pool.shutdownNow();  
        }  
    }  
      
      
    public ExecutorService getCustomThreadPoolExecutor() {  
        return this.pool;  
    }  
      
    private class CustomThreadFactory implements ThreadFactory {  
  
        private AtomicInteger count = new AtomicInteger(0);  
          
        @Override  
        public Thread newThread(Runnable r) {  
            Thread t = new Thread(r);  
            String threadName = TestThreadPoolExecutor.class.getSimpleName() + count.addAndGet(1);  
            System.out.println(threadName);  
            t.setName(threadName);  
            return t;  
        }  
    }  
      
      
    /**
     * 重写拒绝策略
     * @author lzhcode
     *
     */
    private class CustomRejectedExecutionHandler implements RejectedExecutionHandler {  
  
        @Override  
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {  
            // 记录异常  
            // 报警处理等  
            System.out.println("error.............");  
            
            //定制属于自己的阻塞线程池
            // 核心改造点，由blockingqueue的offer改成put阻塞方法  
            // 当提交任务被拒绝时，进入拒绝机制，我们实现拒绝方法，
            // 把任务重新用阻塞提交方法put提交，实现阻塞提交任务功能，防止队列过大，OOM，提交被拒绝方法在下面

/*             try {
				executor.getQueue().put(r);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} */ 
        }  
    }  
      
      
      
    // 测试构造的线程池  
    public static void main(String[] args) {  
    	TestThreadPoolExecutor exec = new TestThreadPoolExecutor();  
        // 1.初始化  
        exec.init();  
          
        ExecutorService pool = exec.getCustomThreadPoolExecutor();  
        for(int i=1; i<100; i++) {  
            System.out.println("提交第" + i + "个任务!");  
            pool.execute(new Runnable() {  
                @Override  
                public void run() {  
                    try {  
                        Thread.sleep(3000);  
                    } catch (InterruptedException e) {  
                        e.printStackTrace();  
                    }  
                    System.out.println("running=====");  
                }  
            });  
        }  
          
          
          
        // 2.销毁----此处不能销毁,因为任务没有提交执行完,如果销毁线程池,任务也就无法执行了  
        // exec.destory();  
          
        try {  
            Thread.sleep(10000);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
    }  
}
/*方法中建立一个核心线程数为30个，缓冲队列有10个的线程池。每个线程任务，执行时会先睡眠3秒，
保证提交10任务时，线程数目被占用完，再提交30任务时，阻塞队列被占用完，
这样提交第41个任务是，会交给CustomRejectedExecutionHandler 异常处理类来处理。

以后提交的任务就不能正常处理了，因为，execute中提交到任务队列是用的offer方法，
如上面代码，这个方法是非阻塞的，所以就会交给CustomRejectedExecutionHandler 来处理，
所以对于大数据量的任务来说，这种线程池，如果不设置队列长度会OOM，设置队列长度，会有任务得不到处理，
接下来我们构建一个阻塞的自定义线程池
*/
