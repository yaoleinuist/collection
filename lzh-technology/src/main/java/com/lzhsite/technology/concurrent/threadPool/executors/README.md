Executors 类
	   需要注意的是 Executors 是一个类，不是 Executor 的复数形式。该类是一个辅助类，此包中所定义的 Executor、ExecutorService、ScheduledExecutorService、ThreadFactory 和 
	   Callable 类的工厂和实用方法。
	此类支持以下各种方法：
	• 创建并返回设置有常用配置字符串的 ExecutorService 的方法。
	• 创建并返回设置有常用配置字符串的 ScheduledExecutorService 的方法。
	• 创建并返回“包装的”ExecutorService 方法，它通过使特定于实现的方法不可访问来禁用重新配置。
	• 创建并返回 ThreadFactory 的方法，它可将新创建的线程设置为已知的状态。
	•创建并返回非闭包形式的 Callable 的方法，这样可将其用于需要 Callable 的执行方法中。
	 Executors 提供了以下一些 static 的方法：
	callable(Runnable task): 将 Runnable 的任务转化成 Callable 的任务
	newCachedThreadPool(): 产生一个 ExecutorService 对象，这个对象带有一个线程池，线程池的大小会根据需要调整，线程执行完任务后返回线程池，供执行下一次任务使用。
	newFixedThreadPool(int poolSize) ： 产生一个 ExecutorService 对象，这个对象带有一个大小为 poolSize 的线程池，若任务数量大于 poolSize ，任务会被放在一个 queue 里顺序执行。
	newSingleThreadScheduledExecutor ：产生一个 ScheduledExecutorService 对象，这个对象的线程池大小为 1 ，若任务多于一个，任务将按先后顺序执行。
	newScheduledThreadPool(int poolSize): 产生一个 ScheduledExecutorService 对象，这个对象的线程池大小为 poolSize ，若任务数量大于 poolSize ，任务会在一个 queue 里等待执行。