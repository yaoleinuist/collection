package com.technology.grammar.ref;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;

import org.junit.Test;

public class TestPhantomRefFinalize {

	ReferenceQueue<MyObject> phantomQueue=null;
	HashMap map=new HashMap();
	public class CheckRefQueue extends Thread{
		
		@Override
		public void run(){
			while(true){
				if(phantomQueue!=null){
						Reference<MyObject> obj;
						try {
							obj = (Reference<MyObject>) phantomQueue.remove();
							Object value=map.get(obj);
							System.out.println("clean resouce:"+value);
							map.remove(obj);
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
		map.put(phantomRef, "db resource");
		new CheckRefQueue().start();
		obj=null;
		Thread.sleep(1000);
		int i=1;
		while(true){
			System.out.println("��"+i+++"��gc");
			System.gc();
			Thread.sleep(1000);
		}
	}
}
