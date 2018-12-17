package com.lzhsite.technology.concurrent.framwork.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import org.junit.Test;

public class CountTask extends RecursiveTask<Integer> {

    private int start;
    private int end;

    private static final int THRED_HOLD = 30;


    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        boolean canCompute = (end - start) <= THRED_HOLD;
        if (canCompute) { // 不需要拆分
            for (int i = start; i <= end; i++) {
                sum += i;
            }

            System.out.println("thread: " + Thread.currentThread() + " start: " + start + " end: " + end);
        } else {
            int mid = (end + start) / 2;
            CountTask left = new CountTask(start, mid);
            CountTask right = new CountTask(mid + 1, end);
            left.fork();
            right.fork();

            sum = left.join() + right.join();
        }
        return sum;
    }
    
    @Test
    public void testFork() throws ExecutionException, InterruptedException {
        int start = 0;
        int end = 200;

        CountTask task = new CountTask(start, end);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Future<Integer> ans = pool.submit(task);
        int sum = ans.get();
        System.out.println(sum);
    }
}