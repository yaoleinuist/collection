package com.lzhsite.technology.concurrent.queue.blockQueue;

import java.util.LinkedList;
import java.util.Queue;
/**
 * 实现一个简单的阻塞队列
 * @author lzhcode
 *
 */
public class BlockingQ {
	private Object notEmpty = new Object();
	private Object notFull = new Object();
	private Queue<Object> linkedList = new LinkedList<Object>();
	private int maxLength = 10;

	public Object take() throws InterruptedException {
		synchronized (notEmpty) {
			if (linkedList.size() == 0) {
				notEmpty.wait();
			}
			synchronized (notFull) {
				if (linkedList.size() == maxLength) {
					notFull.notifyAll();
				}
				return linkedList.poll();
			}
		}
	}

	public void offer(Object object) throws InterruptedException {
		synchronized (notEmpty) {
			if (linkedList.size() == 0) {
				notEmpty.notifyAll();
			}
			synchronized (notFull) {
				if (linkedList.size() == maxLength) {
					notFull.wait();
				}
				linkedList.add(object);
			}
		}
	}
}
