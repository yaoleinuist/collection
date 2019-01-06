package com.lzhsite.technology.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Semaphore类是一个计数信号量，必须由获取它的线程释放， 通常用于限制可以访问某些资源（物理或逻辑的）线程数目。
 * 一个信号量有且仅有3种操作，且它们全部是原子的：初始化、增加和减少 增加可以为一个进程解除阻塞； 减少可以让一个进程进入阻塞。
 * 
 * 面试题思考： 在很多情况下，可能有多个线程需要访问数目很少的资源。假想在服务器上运行着若干个回答客户端请求的线程。这些线程需要连接到同一数据库，但任一时刻
 * 只能获得一定数目的数据库连接。你要怎样才能够有效地将这些固定数目的数据库连接分配给大量的线程？
 * 
 * 答：
 * 
 * 1.给方法加同步锁，保证同一时刻只能有一个人去调用此方法，其他所有线程排队等待，但是此种情况下即使你的数据库链接有10个，也始终只有一个处于使
 * 
 * 用状态。这样将会大大的浪费系统资源，而且系统的运行效率非常的低下。
 * 
 * 
 * 2.另外一种方法当然是使用信号量，通过信号量许可与数据库可用连接数相同的数目，将大大的提高效率和性能。
 * 
 * @author lzhcode
 * 
 */
public class TestSemaphore {
	public static void main(String[] args) {
		ExecutorService service = Executors.newCachedThreadPool();
		final Semaphore sp = new Semaphore(3);
		for (int i = 0; i < 10; i++) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						sp.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					System.out.println(
							"线程" + Thread.currentThread().getName() + "进入，当前已有" + (3 - sp.availablePermits()) + "个并发");
					try {
						Thread.sleep((long) (Math.random() * 10000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("线程" + Thread.currentThread().getName() + "即将离开");
					// 离开的瞬间其他线程可以再进入所以下面的代码没法和前面的代码构成原子单元
					sp.release();
					// 下面代码有时候执行不准确，因为其没有和上面的代码合成原子单元
					System.out.println(
							"线程" + Thread.currentThread().getName() + "已离开，当前已有" + (3 - sp.availablePermits()) + "个并发");
				}
			};
			service.execute(runnable);
		}
	}

}
