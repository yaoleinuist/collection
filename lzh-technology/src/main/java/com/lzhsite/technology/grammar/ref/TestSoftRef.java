package com.lzhsite.technology.grammar.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import org.junit.Test;

public class TestSoftRef {

	ReferenceQueue<MyObject> softQueue=null;
	
	public class CheckRefQueue extends Thread{
		
		@SuppressWarnings("unchecked")
		@Override
		public void run(){
			while(true){
				if(softQueue!=null){
						Reference<MyObject> obj=null;
							try {
								obj = (Reference<MyObject>) softQueue.remove();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if(obj!=null)
							System.out.println("Object for SoftReference is "+obj.get());

				}
			}
		}
	}
//	public class MemConsumer extends Thread{
//		HashMap map=new HashMap();
//		@Override
//		public void run(){
//			for(int i=0;i<10000;i++){
//				map.put(i, new StringBuffer().append(i));
//			}
//		}
//	}
	
	@Test
	public void test() throws InterruptedException {
		MyObject obj=new MyObject();
		softQueue = new ReferenceQueue<MyObject>();  
		SoftReference<MyObject> softRef=new SoftReference<MyObject>(obj,softQueue);
		
		new CheckRefQueue().start();
		//ȥ��ǿ����
		obj=null;
		System.gc();
		System.out.println("After GC:Soft Get= " + softRef.get());
		System.out.println("�������ڴ�");
		byte[] b=new byte[4*1024*925];
		System.out.println("After new byte[]:Soft Get= " + softRef.get());
	}

	//@Test
	public void testSimple() throws InterruptedException {
		MyObject obj=new MyObject();

		SoftReference<MyObject> softRef=new SoftReference<MyObject>(obj);
		//ȥ��ǿ����
		obj=null;
		System.out.println("Soft Get: " + softRef.get());
		System.gc();
		System.out.println("Soft Get: " + softRef.get());
		byte[] b=new byte[4*1024*925];
		System.out.println("Soft Get: " + softRef.get());
	}
}
