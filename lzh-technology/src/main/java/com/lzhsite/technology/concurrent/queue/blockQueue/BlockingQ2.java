package com.lzhsite.technology.concurrent.queue.blockQueue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/*
	 实现一个简单的阻塞队列(2)
	要执行await操作，必须先取得该Condition的锁。
	执行await操作之后，锁会释放。
	被唤醒之前，需要先获得锁。
	一个锁可以创建多个Condition
	注意： 未锁就直接执行await、 signal、 siganlAll会抛异常
*/
public class BlockingQ2 {
	
	private Lock lock = new ReentrantLock();
	private Condition notEmpty = lock.newCondition();
	private Condition notFull = lock.newCondition();
	private Queue<Object> linkedList = new LinkedList<Object>();
	private int maxLength = 10;

	public Object take() throws InterruptedException {
		lock.lock();
		try {
			if (linkedList.size() == 0) {
				notEmpty.await();
			}
			if (linkedList.size() == maxLength) {
				notFull.signalAll();
			}
			return linkedList.poll();
		} finally {
			lock.unlock();
		}
	}

	public void offer(Object object) throws InterruptedException {
		lock.lock();
		try {
			if (linkedList.size() == 0) {
				notEmpty.signalAll();
			}
			if (linkedList.size() == maxLength) {
				notFull.await();
			}
			linkedList.add(object);
		} finally {
			lock.unlock();
		}
	}

}
