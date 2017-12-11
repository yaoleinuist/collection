package com.lzhsite.technology.redis.testRedislockframework;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;

import com.lzhsite.technology.redis.redislockframework.CacheLockInterceptor;
import com.lzhsite.technology.redis.redislockframework.RedisClient;
import com.lzhsite.technology.redis.testRedislockframework.impl.SecKillImpl;

import redis.clients.jedis.JedisPool;
/**
 * 
 * 我们来假设一个最简单的秒杀场景：数据库里有一张表，column分别是商品ID，和商品ID对应的库存量，
 * 秒杀成功就将此商品库存量-1。现在假设有1000个线程来秒杀两件商品，500个线程秒杀第一个商品，
 * 500个线程秒杀第二个商品。我们来根据这个简单的业务场景来解释一下分布式锁。 
 * 通常具有秒杀场景的业务系统都比较复杂，承载的业务量非常巨大，并发量也很高。
 * 这样的系统往往采用分布式的架构来均衡负载。那么这1000个并发就会是从不同的地方过来，
 * 商品库存就是共享的资源，也是这1000个并发争抢的资源，这个时候我们需要将并发互斥管理起来。
 * 这就是分布式锁的应用。 
 * 
 * 这里采用了动态代理的方法，利用注解和反射机制得到分布式锁ID，进行加锁和释放锁操作。
 * 当然也可以直接在方法进行这些操作，采用动态代理也是为了能够将锁操作代码集中在代理中，便于维护。 
 * 通常秒杀场景发生在web项目中，可以考虑利用spring的AOP特性将锁操作代码置于切面中，
 * 当然AOP本质上也是动态代理。
 * @author lzhcode
 *
 */
public class SecKillTest {
	
	private static Long commidityId1 = 10000001L;
	private static Long commidityId2 = 10000002L;
 
	@Test
    public void testSecKill(){
        int threadCount = 1600;
        int splitPoint = 800;
        final CountDownLatch endCount = new CountDownLatch(threadCount);
        final CountDownLatch beginCount = new CountDownLatch(1);
        final SecKillImpl testClass = new SecKillImpl();

        Thread[] threads = new Thread[threadCount];
        //起500个线程，秒杀第一个商品
        for(int i= 0;i < splitPoint;i++){
            threads[i] = new Thread(new  Runnable() {
                public void run() {
                    try {
                        //等待在一个信号量上，挂起
                        beginCount.await();
                        //用动态代理的方式调用secKill方法
                        SeckillInterface proxy = (SeckillInterface) Proxy.newProxyInstance(SeckillInterface.class.getClassLoader(), 
                            new Class[]{SeckillInterface.class}, new CacheLockInterceptor(testClass));
                        proxy.secKill("test", commidityId1);
                        endCount.countDown();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();

        }
        //再起500个线程，秒杀第二件商品
        for(int i= splitPoint;i < threadCount;i++){
            threads[i] = new Thread(new  Runnable() {
                public void run() {
                    try {
                        //等待在一个信号量上，挂起
                        beginCount.await();
                        //用动态代理的方式调用secKill方法
                        SeckillInterface proxy = (SeckillInterface) Proxy.newProxyInstance(SeckillInterface.class.getClassLoader(), 
                            new Class[]{SeckillInterface.class}, new CacheLockInterceptor(testClass));
                        proxy.secKill("test", commidityId2);
                        endCount.countDown();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();

        }


        long startTime = System.currentTimeMillis();
        //主线程释放开始信号量，并等待结束信号量，这样做保证1000个线程做到完全同时执行，保证测试的正确性
        beginCount.countDown();

        try {
            //主线程等待结束信号量
            endCount.await();
            //观察秒杀结果是否正确
            System.out.println(SecKillImpl.inventory.get(commidityId1));
            System.out.println(SecKillImpl.inventory.get(commidityId2));
            System.out.println("error count " + CacheLockInterceptor.ERROR_COUNT);
            System.out.println("total cost " + (System.currentTimeMillis() - startTime));
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
