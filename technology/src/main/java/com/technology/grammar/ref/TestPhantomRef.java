package com.technology.grammar.ref;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

import org.junit.Test;

public class TestPhantomRef {

	ReferenceQueue<MyObject> phantomQueue=null;
	
	public class CheckRefQueue extends Thread{
		
		@Override
		public void run(){
			while(true){
				if(phantomQueue!=null){
						Reference<MyObject> obj;
						try {
							obj = (Reference<MyObject>) phantomQueue.remove();
							System.out.println("Object for PhantomReference is "+obj.get());
							System.exit(0);
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
		phantomQueue = new ReferenceQueue<MyObject>();  
		PhantomReference<MyObject> phantomRef=new PhantomReference<MyObject>(obj,phantomQueue);
		System.out.println("Phantom Get: " + phantomRef.get());
		new CheckRefQueue().start();
		obj=null;
		Thread.sleep(1000);
		//ȥ��ǿ����
		int i=1;
		while(true){
			System.out.println("��"+i+++"��gc");
			System.gc();
			Thread.sleep(1000);
		}
	}
	

}
