package com.lzhsite.technology.concurrent.join;
/**
 * 让父线程等待子线程结束之后才能继续运行。
 * @author lzhcode
 *
 */
public class TestJoin {
	public static void main(String[] args) {
		Thread t = new Thread(new RunnableImpl());
		t.start();
		try {
			t.join(1000); // 主线程只等1 秒，不管子线程什么时候结束
			System.out.println("joinFinish");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

class RunnableImpl implements Runnable {
	@Override
	public void run() {
		try {
			System.out.println("Begin sleep");
			Thread.sleep(1000);
			System.out.println("End sleep");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
/*结果是：
Begin sleep
End sleep
joinFinish
 当main 线程调用t.join 时，main 线程等待t 线程 ，等待时间是1000 ，如果t 线程Sleep 2000 呢
public   void  run() {
try  {
System.out.println("Begin sleep");
// Thread.sleep(1000);
Thread.sleep(2000) ;
System.out.println("End sleep");
}  catch  (InterruptedException e) {
e.printStackTrace();
}
}
结果是：
Begin sleep
joinFinish
End sleep 

*/