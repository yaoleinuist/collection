package com.technology.grammar.ref;

import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import org.junit.Test;

public class TestWeakRef {

	ReferenceQueue<MyObject> weakQueue=null;
	
	public class CheckRefQueue extends Thread{
		
		@Override
		public void run(){
			while(true){
				if(weakQueue!=null){
						Reference<MyObject> obj;
						try {
							obj = (Reference<MyObject>) weakQueue.remove();
							System.out.println("Object for WeakReference is "+obj.get());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	
	@Test
	public void test() throws InterruptedException {
		MyObject obj=new MyObject();
		weakQueue = new ReferenceQueue<MyObject>();  
		WeakReference<MyObject> weakRef=new WeakReference<MyObject>(obj,weakQueue);
		new CheckRefQueue().start();
		//ȥ��ǿ����
		obj=null;
		System.out.println("Before GC:Weak Get= " + weakRef.get());
		System.gc();
		System.out.println("After GC:Weak Get= " + weakRef.get());
	}

}
