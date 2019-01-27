package com.lzhsite.technology.concurrent.lock.reetrantLock;

import java.util.concurrent.atomic.AtomicReference;
/**
 * 可重入锁的简单实现
 * @author lzhcode
 *
 */
public class ReentrantLock {
	
	private AtomicReference<Thread> owner = new AtomicReference<Thread>();
	   private int state = 0;

	   public void lock() {
	       Thread current = Thread.currentThread();
	       if (current == owner.get()) {
	           state++;
	           return;
	       }
	       //这句是很经典的“自旋”式语法，AtomicInteger中也有
	       for (;;) {
	    	   //是null的话更新为current返回true，否者不做任何更新返回false
	           if (!owner.compareAndSet(null, current)) {
	               return;
	           }
	       }
	   }

	   public void unlock() {
	       Thread current = Thread.currentThread();
	       if (current == owner.get()) {
	           if (state != 0) {
	               state--;
	           } else {
	               owner.compareAndSet(current, null);
	           }
	       }
	   }
}
