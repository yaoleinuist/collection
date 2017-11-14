package com.lzhsite.technology.thread.testLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * 
 *    什么是可重入锁 ：可重入锁，也叫做递归锁，指的是同一线程 外层函数获得锁之后 ，内层递归函数仍然有获取该锁的代码，但不受影响。
 * 
 *    什么叫读写锁 ：读写锁拆成读锁和写锁来理解。
 *                  读锁可以共享，多个线程可以同时拥有读锁，但是写锁却只能只有一个线程拥有，
 *                  而且获取写锁的时候其他线程都已经释放了读锁，而且该线程获取写锁之后，
 *                  其他线程不能再获取读锁。简单的说就是写锁是排他锁，读锁是共享锁。 
 * 
 *  类ReentrantLock实现了Lock,它拥有与Sychronized相同的并发性和内存语义，
 *  但是添加了类似锁投票、定时锁等候和可中断等候的一些特性。
 *  此外，它还提供了在与激烈争用情况下更佳的性能
 * @author lzh
 *
 */
public class TestReentrantReadWriteLock {

	public static void main(String[] args) {
        BankCard bc = new BankCard();  
      //  Lock lock = new ReentrantLock();  
        ReadWriteLock lock = new  ReentrantReadWriteLock();  
        ExecutorService pool = Executors.newCachedThreadPool();  
        Consumer consumer = new Consumer(bc, lock);  
        Producer producer1 = new Producer(bc, lock,1);  
        Producer producer2 = new Producer(bc, lock,2);  
        pool.execute(consumer);  
        pool.execute(producer1);  
        pool.execute(producer2);  
 
	}
}
