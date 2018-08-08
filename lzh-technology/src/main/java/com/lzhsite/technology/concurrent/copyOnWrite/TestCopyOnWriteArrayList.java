package com.lzhsite.technology.concurrent.copyOnWrite;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
//http://blog.csdn.net/linsongbin1/article/details/54581787
//https://www.cnblogs.com/dolphin0520/p/3938914.html


/**
 * CopyOnWrite容器有很多优点，但是同时也存在两个问题，即内存占用问题和数据一致性问题。所以在开发的时候需要注意一下,
 * 合适读多写少的场景，不过这类慎用 
 * 
 *　CopyOnWrite容器即写时复制的容器。通俗的理解是当我们往一个容器添加元素的时候，不直接往当前容器添加，而是先将当前容器进行Copy，
 * 复制出一个新的容器，然后新的容器里添加元素，添加完元素之后，再将原容器的引用指向新的容器。这样做的好处是我们可以对CopyOnWrite容器进行并发的读，
 * 而不需要加锁，因为当前容器不会添加任何元素。
 * 
 * 如上面的分析CopyOnWriteArrayList表达的一些思想： 
 *	1、读写分离，读和写分开 
 *	2、最终一致性 
 * 	3、使用另外开辟空间的思路，来解决并发冲突
 * 
 * @author lzhcode
 *
 */

public class TestCopyOnWriteArrayList {
	private void test() throws InterruptedException {
		
		CountDownLatch countDownLatch=new CountDownLatch(8);
		
		// 1、初始化CopyOnWriteArrayList
		List<Integer> tempList = Arrays.asList(new Integer[] { 1, 2 });
		CopyOnWriteArrayList<Integer> copyList = new CopyOnWriteArrayList<>(tempList);

		// 2、模拟多线程对list进行读和写
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		executorService.execute(new ReadThread(copyList,countDownLatch));
		executorService.execute(new WriteThread(copyList,countDownLatch));
		executorService.execute(new WriteThread(copyList,countDownLatch));
		executorService.execute(new WriteThread(copyList,countDownLatch));
		executorService.execute(new ReadThread(copyList,countDownLatch));
		executorService.execute(new WriteThread(copyList,countDownLatch));
		executorService.execute(new ReadThread(copyList,countDownLatch));
		executorService.execute(new WriteThread(copyList,countDownLatch));
		countDownLatch.await(); 
		System.out.println("copyList size:" + copyList.size());
		
		
	}

	public static void main(String[] args) throws InterruptedException {
		new TestCopyOnWriteArrayList().test();
	}
//	运行上面的代码,没有报出
//	java.util.ConcurrentModificationException
//	说明了CopyOnWriteArrayList并发多线程的环境下，仍然能很好的工作。
}

class ReadThread implements Runnable {
	private List<Integer> list;
	private CountDownLatch countDownLatch;
	
	public ReadThread(List<Integer> list, CountDownLatch countDownLatch) {
		this.list = list;
		this.countDownLatch =countDownLatch;
	}

	@Override
	public void run() {
		System.out.print("ReadThread"+Thread.currentThread()+":");
		for (Integer ele : list) {
			System.out.print(ele+" ");
		}
		System.out.println();
		countDownLatch.countDown(); 
	}
}

class WriteThread implements Runnable {
	
	private List<Integer> list;
	private CountDownLatch countDownLatch;
	
	public WriteThread(List<Integer> list, CountDownLatch countDownLatch) {
		this.list = list;
		this.countDownLatch =countDownLatch;
	}

	@Override
	public void run() {
		this.list.add(9);
		countDownLatch.countDown(); 
	}
}
