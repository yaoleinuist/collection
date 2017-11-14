package com.lzhsite.technology.grammar.ref;

public class CanReliveObj {
	public static CanReliveObj obj;
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("CanReliveObj finalize called");
		obj=this;
	}
	@Override
	public String toString(){
		return "I am CanReliveObj";
	}
	public static void main(String[] args) throws InterruptedException{
		obj=new CanReliveObj();
		obj=null;
		System.gc();
		Thread.sleep(1000);
		if(obj==null){
			System.out.println("obj �� null");
		}else{
			System.out.println("obj ����");
		}
		System.out.println("�ڶ���gc");
		//obj=null;
		System.gc();
		Thread.sleep(1000);
		if(obj==null){
			System.out.println("obj �� null");
		}else{
			System.out.println("obj ����");
		}
	}
}
