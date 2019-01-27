package com.lzhsite.technology.grammar.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import org.junit.Test;

public class TestAllRef {

	ReferenceQueue<MyObject> softQueue = null;
	ReferenceQueue<MyObject> weakQueue = null;
	ReferenceQueue<MyObject> phantomQueue = null;

	public class CheckRefQueue extends Thread {
		protected ReferenceQueue<MyObject> queue;
		protected String queueName;

		public CheckRefQueue(ReferenceQueue<MyObject> q, String queueName) {
			queue = q;
			this.queueName = queueName;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			while (true) {
				Reference<MyObject> obj = null;
				try {
					obj = (Reference<MyObject>) queue.remove();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (obj != null)
					System.out.println("Object for "+queueName+" is "
							+ obj.get());
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Test
	public void test() throws InterruptedException {
		MyObject obj = new MyObject();
		softQueue = new ReferenceQueue<MyObject>();
		weakQueue = new ReferenceQueue<MyObject>();
		phantomQueue = new ReferenceQueue<MyObject>();
		SoftReference<MyObject> softRef = new SoftReference<MyObject>(obj,
				softQueue);
		WeakReference<MyObject> weakRef = new WeakReference<MyObject>(obj,
				weakQueue);
		PhantomReference<MyObject> phantomRef = new PhantomReference<MyObject>(
				obj, phantomQueue);

		new CheckRefQueue(softQueue,"soft queue").start();
		new CheckRefQueue(weakQueue,"weak queue").start();
		new CheckRefQueue(phantomQueue,"phantom queue").start();
		
		// 去除强引用
		obj = null;
		System.out.println("Before GC:Soft Get= " + softRef.get());
		System.out.println("Before GC:weak Get= " + weakRef.get());
		System.out.println("Before GC:phantom Get= " + phantomRef.get());
		System.gc();
		System.out.println("After GC:Soft Get= " + softRef.get());
		System.out.println("After GC:weak Get= " + weakRef.get());
		System.out.println("After GC:phantom Get= " + phantomRef.get());
		System.out.println("大内存分配");
		byte[] b = new byte[4 * 1024 * 925];
		System.out.println("After GC:Soft Get= " + softRef.get());
		System.out.println("After GC:weak Get= " + weakRef.get());
		System.out.println("After GC:phantom Get= " + phantomRef.get());

	}

}

