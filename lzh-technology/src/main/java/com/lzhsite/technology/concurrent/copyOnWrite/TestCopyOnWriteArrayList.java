package com.lzhsite.technology.concurrent.copyOnWrite;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
 
//http://blog.csdn.net/linsongbin1/article/details/54581787
//https://www.cnblogs.com/dolphin0520/p/3938914.html


//　CopyOnWrite容器有很多优点，但是同时也存在两个问题，即内存占用问题和数据一致性问题。所以在开发的时候需要注意一下。

public class TestCopyOnWriteArrayList {
	private void test() {
		// 1、初始化CopyOnWriteArrayList
		List<Integer> tempList = Arrays.asList(new Integer[] { 1, 2 });
		CopyOnWriteArrayList<Integer> copyList = new CopyOnWriteArrayList<>(tempList);

		// 2、模拟多线程对list进行读和写
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		executorService.execute(new ReadThread(copyList));
		executorService.execute(new WriteThread(copyList));
		executorService.execute(new WriteThread(copyList));
		executorService.execute(new WriteThread(copyList));
		executorService.execute(new ReadThread(copyList));
		executorService.execute(new WriteThread(copyList));
		executorService.execute(new ReadThread(copyList));
		executorService.execute(new WriteThread(copyList));

		System.out.println("copyList size:" + copyList.size());
	}

	public static void main(String[] args) {
		new TestCopyOnWriteArrayList().test();
	}
}

class ReadThread implements Runnable {
	private List<Integer> list;

	public ReadThread(List<Integer> list) {
		this.list = list;
	}

	@Override
	public void run() {
		for (Integer ele : list) {
			System.out.println("ReadThread:" + ele);
		}
	}
}

class WriteThread implements Runnable {
	private List<Integer> list;

	public WriteThread(List<Integer> list) {
		this.list = list;
	}

	@Override
	public void run() {
		this.list.add(9);
	}
}
