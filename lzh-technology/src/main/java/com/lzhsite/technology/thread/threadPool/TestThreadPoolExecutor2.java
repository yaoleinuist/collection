package com.lzhsite.technology.thread.threadPool;

import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*说明： 
	http://blog.chinaunix.net/uid-20577907-id-3519578.html
	1、在这段程序中，一个任务就是一个Runnable类型的对象，也就是一个ThreadPoolTask类型的对象。 
	2、一般来说任务除了处理方式外，还需要处理的数据，处理的数据通过构造方法传给任务。 
	3、在这段程序中，main()方法相当于一个残忍的领导，他派发出许多任务，丢给一个叫 threadPool的任劳任怨的小组来做。 
	这个小组里面队员至少有两个，如果他们两个忙不过来，任务就被放到任务列表里面。 
	如果积压的任务过多，多到任务列表都装不下(超过3个)的时候，就雇佣新的队员来帮忙。但是基于成本的考虑，不能雇佣太多的队员，至多只能雇佣 4个。 
	如果四个队员都在忙时，再有新的任务，这个小组就处理不了了，任务就会被通过一种策略来处理，我们的处理方式是不停的派发，直到接受这个任务为止(更残忍！呵呵)。 
	因为队员工作是需要成本的，如果工作很闲，闲到 3SECONDS都没有新的任务了，那么有的队员就会被解雇了，但是，为了小组的正常运转，即使工作再闲，小组的队员也不能少于两个。 
	4、通过调整 produceTaskSleepTime和 consumeTaskSleepTime的大小来实现对派发任务和处理任务的速度的控制，改变这两个值就可以观察不同速率下程序的工作情况。 
	5、通过调整4中所指的数据，再加上调整任务丢弃策略，换上其他三种策略，就可以看出不同策略下的不同处理方式。 
	6、对于其他的使用方法，参看jdk的帮助，很容易理解和使用。
 */
public class TestThreadPoolExecutor2 {

	private static int produceTaskSleepTime = 2;
	private static int produceTaskMaxNumber = 10;

	public static void main(String[] args) {
		// 构造一个线程池
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(3), new ThreadPoolExecutor.DiscardOldestPolicy());

		for (int i = 1; i <= produceTaskMaxNumber; i++) {
			try {
				// 产生一个任务，并将其加入到线程池
				String task = "task@ " + i;
				System.out.println("put " + task);
				threadPool.execute(new ThreadPoolTask(task));

				// 便于观察，等待一段时间
				Thread.sleep(produceTaskSleepTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

/**
 * 线程池执行的任务
 */
class ThreadPoolTask implements Runnable, Serializable {
	private static final long serialVersionUID = 0;
	private static int consumeTaskSleepTime = 2000;
	// 保存任务所需要的数据
	private Object threadPoolTaskData;

	ThreadPoolTask(Object tasks) {
		this.threadPoolTaskData = tasks;
	}

	public void run() {
		// 处理一个任务，这里的处理方式太简单了，仅仅是一个打印语句
		System.out.println(Thread.currentThread().getName());
		System.out.println("start .." + threadPoolTaskData);

		try {
			// //便于观察，等待一段时间
			Thread.sleep(consumeTaskSleepTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		threadPoolTaskData = null;
	}

	public Object getTask() {
		return this.threadPoolTaskData;
	}
}
