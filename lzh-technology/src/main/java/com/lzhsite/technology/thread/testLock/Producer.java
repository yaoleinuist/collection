package com.lzhsite.technology.thread.testLock;

import java.util.concurrent.locks.ReadWriteLock;
/**
 * 与互斥锁相比，读-写锁允许对共享数据进行更高级别的并发访问。
 * 虽然一次只有一个线程（writer 线程）可以修改共享数据，
 * 但在许多情况下，任何数量的线程可以同时读取共享数据（reader 线程）
 * 读-写锁利用了这一点。从理论上讲，与互斥锁相比，使用读-写锁所允许的并发性增强将带来更大的性能提高。
 * 在实践中，只有在多处理器上并且只在访问模式适用于共享数据时，才能完全实现并发性增强。
 * 在 writer 释放写入锁时，reader 和 writer 都处于等待状态，
 * 在这时要确定是授予读取锁还是授予写入锁。Writer 优先比较普遍，因为预期写入所需的时间较短并且不那么频繁。
 * Reader 优先不太普遍，因为如果 reader 正如预期的那样频繁和持久，那么它将导致对于写入操作来说较长的时延。
 * 公平或者“按次序”实现也是有可能的。在 reader 处于活动状态而 writer 处于等待状态时，
 * 确定是否向请求读取锁的 reader 授予读取锁。Reader 优先会无限期地延迟 writer，而 writer 优先会减少可能的并发。
 * 
 * 我们来注释父母监督时锁的释放：lock.readLock().unlock(); 
 * 可以看到儿子花了一次钱后，父母把卡给锁了，儿子不能在花钱，但是父母两个人都可以一直查询卡的余额。 
 * @author Administrator
 *
 */
public class Producer  implements Runnable {  
	 BankCard bc = null;  
	    int type = 0;  
	    ReadWriteLock lock = null;  
	    Producer(BankCard bc, ReadWriteLock lock,int type) {  
	        this.bc = bc;  
	        this.lock = lock;  
	        this.type = type;  
	    }  
	    public void run() {  
	        try {  
	            while(true){  
	                lock.readLock().lock();   
	                if(type==2)  
	                    System.out.println("父亲要查询，现在余额：" + bc.getBalance());  
	                else  
	                    System.out.println("老妈要查询，现在余额：" + bc.getBalance());  
	                lock.readLock().unlock();  
	                Thread.sleep(1 * 1000);  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }         
	    }  
}
