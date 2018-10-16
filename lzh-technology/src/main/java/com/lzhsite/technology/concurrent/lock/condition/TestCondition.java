package com.lzhsite.technology.concurrent.lock.condition;
//Condition除了支持上面的功能之外，它更强大的地方在于：能够更加精细的控制多线程的休眠与唤醒。对于同一个锁，
//我们可以创建多个Condition，在不同的情况下使用不同的Condition。
//例如，假如多线程读/写同一个缓冲区：当向缓冲区中写入数据之后，唤醒"读线程"；
//当从缓冲区读出数据之后，唤醒"写线程"；并且当缓冲区满的时候，"写线程"需要等待；当缓冲区为空时，"读线程"需要等待。         
// 如果采用Object类中的wait(), notify(), notifyAll()实现该缓冲区，当向缓冲区写入数据之后需要唤醒"读线程"时，
//不可能通过notify()或notifyAll()明确的指定唤醒"读线程"，而只能通过notifyAll唤醒所有线程
//(但是notifyAll无法区分唤醒的线程是读线程，还是写线程)。但是，通过Condition，就能明确的指定唤醒读线程。
public class TestCondition {
	private static BoundedBuffer bb = new BoundedBuffer();

	public static void main(String[] args) {
		
		// 启动10个“写线程”，向BoundedBuffer中不断的写数据(写入0-9)；
		// 启动10个“读线程”，从BoundedBuffer中不断的读数据。
		for (int i = 0; i < 10; i++) {
			new PutThread("p" + i, i).start();
			new TakeThread("t" + i).start();
		}
	}

	static class PutThread extends Thread {
		private int num;

		public PutThread(String name, int num) {
			super(name);
			this.num = num;
		}

		public void run() {
			try {
				Thread.sleep(1); // 线程休眠1ms
				bb.put(num); // 向BoundedBuffer中写入数据
			} catch (InterruptedException e) {
			}
		}
	}

	static class TakeThread extends Thread {
		public TakeThread(String name) {
			super(name);
		}

		public void run() {
			try {
				Thread.sleep(10); // 线程休眠1ms
				Integer num = (Integer) bb.take(); // 从BoundedBuffer中取出数据
			} catch (InterruptedException e) {
			}
		}
	}
}
