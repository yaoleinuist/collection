package com.technology.thread.join;

/*其实Join 方法实现是通过wait （小提示：Object 提供的方法）。 当main 线程调用t.join 时候，main 线程会获得线程对象t 的锁 （wait 意味着拿到该对象的锁), 调用该对象的wait( 等待时间) ，直到该对象唤醒main 线程，比如退出后。
   这就意味着main 线程调用t.join 时，必须能够拿到线程t 对象的锁 ，如果拿不到它是无法wait 的，刚开的例子t.join(1000) 不是说明了main 线程等待1 秒，如果在它等待之前，其他线程获取了t 对象的锁，它等待时间可不就是1 毫秒了。上代码介绍： */
public class TestJoin2 {
	public static void main(String[] args) {
		Thread t = new Thread(new RunnableImpl2());
		new ThreadTest(t).start();// 这个线程会持有锁
		t.start();
		try {
			t.join();
			System.out.println("joinFinish");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class ThreadTest extends Thread {
	Thread thread;

	public ThreadTest(Thread thread) {
		this.thread = thread;
	}

	@Override
	public void run() {
		holdThreadLock();
	}

	public void holdThreadLock() {
		synchronized (thread) {
			System.out.println("getObjectLock");
			try {
				Thread.sleep(9000);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
			System.out.println("ReleaseObjectLock");
		}
	}
}

class RunnableImpl2 implements Runnable {
	@Override
	public void run() {
		try {
			System.out.println("Begin sleep");
			Thread.sleep(2000);
			System.out.println("End sleep");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
/*在main 方法中   通过new  ThreadTest(t).start(); 实例化ThreadTest   线程对象， 它在 holdThreadLock() 方法中，通过   synchronized  (thread) ，获取线程对象t 的锁，并Sleep （9000 ）后释放，这就意味着，即使
main 方法t.join(1000), 等待一秒钟，它必须等待 ThreadTest   线程释放t 锁后才能进入wait 方法中，它实际等待时间是9000+1000 MS
运行结果是：
getObjectLock
Begin sleep
End sleep
ReleaseObjectLock

joinFinish*/