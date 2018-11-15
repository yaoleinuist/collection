package com.lzhsite.jvm;

public class TestReDeadLock {
	public static void main(String[] args) throws Exception {
		A a = new A();
		B b = new B();
		C c = new C();
		D d = new D();
		
		a.next = b;
		b.next = c;
		c.next = d;
		d.next = a;
		//以上将调用流程定义好
		Thread at = new Thread(a, "AThread");
		Thread bt = new Thread(b, "BThread");
		Thread ct = new Thread(c, "CThread");
		Thread dt = new Thread(d, "DThread");
		//启动线程
		at.start();
		Thread.sleep(10);
		bt.start();
		Thread.sleep(10);
		ct.start();
		Thread.sleep(10);
		dt.start();

	}

}

class A extends P implements Runnable {

	P next;

	public A() {
	};

	public synchronized void getRes() {
		System.out.println("当前线程：" + Thread.currentThread().getName()
				+ " | 进入了" + this.getClass().getSimpleName() + " 获取到资源 | 准备调用："
				+ next.getClass().getSimpleName());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		next.getRes();
	}

	@Override
	public void run() {
		getRes();
	}

}

class B extends P implements Runnable {

	P next;

	public synchronized void getRes() {
		System.out.println("当前线程：" + Thread.currentThread().getName()
				+ " | 进入了" + this.getClass().getSimpleName() + " 获取到资源 | 准备调用："
				+ next.getClass().getSimpleName());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		next.getRes();
	}

	@Override
	public void run() {
		getRes();
	}

}

class C extends P implements Runnable {

	P next;

	public synchronized void getRes() {
		System.out.println("当前线程：" + Thread.currentThread().getName()
				+ " | 进入了" + this.getClass().getSimpleName() + " 获取到资源 | 准备调用："
				+ next.getClass().getSimpleName());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		next.getRes();
	}

	@Override
	public void run() {
		getRes();
	}

}

class D extends P implements Runnable {

	P next;

	public synchronized void getRes() {
		System.out.println("当前线程：" + Thread.currentThread().getName()
				+ " | 进入了" + this.getClass().getSimpleName() + " 获取到资源 | 准备调用："
				+ next.getClass().getSimpleName());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		next.getRes();
	}

	@Override
	public void run() {
		getRes();
	}

}

class P {
	public synchronized void getRes() {
	}
}