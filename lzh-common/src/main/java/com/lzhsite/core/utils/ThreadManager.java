package com.lzhsite.core.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ThreadManager {
    public static ThreadPool instance;

    /**
     * 获取单例的线程池对象
     */
    public static ThreadPool getInstance() {
        if (instance == null) {
            synchronized (ThreadManager.class) {
                if (instance == null) {
                    // 获取处理器数量
                    int cpuNum = Runtime.getRuntime().availableProcessors();
                    // 根据cpu数量,计算出合理的线程并发数
                    int threadNum = cpuNum * 2 + 1;
                    // 默认是双核的cpu 每个核心走一个线程 一个等待线程
                    instance = new ThreadPool(threadNum - 1, threadNum, Integer.MAX_VALUE);
                }
            }
        }
        return instance;
    }

    public static class ThreadPool {
        private ThreadPoolExecutor threadPoolExecutor;
        private int corePoolSize;
        private int maximumPoolSize;
        private long keepAliveTime;

        private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
            this.corePoolSize = corePoolSize;
            this.maximumPoolSize = maximumPoolSize;
            this.keepAliveTime = keepAliveTime;
        }

        public void execute(Runnable runnable) {
            if (runnable == null) {
                return;
            }
            if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
                threadPoolExecutor = new ThreadPoolExecutor(corePoolSize,// 核心线程数
                        maximumPoolSize,// 最大线程数
                        keepAliveTime,// 当线程空闲时，线程存活时间
                        TimeUnit.MILLISECONDS,// 时间单位
                        new LinkedBlockingDeque<>(Integer.MAX_VALUE),// 线程任务队列
                        Executors.defaultThreadFactory(),// 线程工厂
                        new ThreadPoolExecutor.AbortPolicy() {// 队列已满,而且当前线程数已经超过最大线程数时的异常处理策略
                            @Override
                            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                                super.rejectedExecution(r, e);
                            }
                        }
                );
            }
            threadPoolExecutor.execute(runnable);
        }

        /**
         * 从线程队列中移除对象
         */
        public void cancel(Runnable runnable) {
            if (threadPoolExecutor != null) {
                threadPoolExecutor.getQueue().remove(runnable);
            }
        }
    }
}
