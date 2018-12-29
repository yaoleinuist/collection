package com.lzhsite.technology.concurrent.threadPool.executors;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * newSingleThreadScheduledExecutor产生一个 ScheduledExecutorService 对象，这个对象的线程池大小为 1 ，若任务多于一个，任务将按先后顺序执行。
 * http://www.bubuko.com/infodetail-1286245.html
 * @author lzhcode
 *
 */
public class TestNewSingleThreadScheduled {
    private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws Exception {
        final String jobID = "my_job_1";
        final AtomicInteger count = new AtomicInteger(0);
        final Map<String, Future> futures = new HashMap<>();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        Future future = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                System.out.println(count.getAndIncrement());

                if (count.get() > 10) {
                    Future future = futures.get(jobID);
                    if (future != null) future.cancel(true);
                    countDownLatch.countDown();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        futures.put(jobID, future);
        countDownLatch.await();

        scheduler.shutdown();
    }
}