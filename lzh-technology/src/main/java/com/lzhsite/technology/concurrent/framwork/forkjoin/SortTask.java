package com.lzhsite.technology.concurrent.framwork.forkjoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.junit.Test;

public class SortTask extends RecursiveTask<List<Integer>> {

    private List<Integer> list;

    private final static int THRESHOLD = 5;

    public SortTask(List<Integer> list) {
        this.list = list;
    }

    @Override
    protected List<Integer> compute() {
        if (list.size() < THRESHOLD) {
            Collections.sort(list);

            System.out.println("thread: " + Thread.currentThread() + " sort: " + list);
            return list;
        }


        int mid = list.size() >> 1;


        SortTask l = new SortTask(list.subList(0,  mid));
        SortTask r = new SortTask(list.subList(mid, list.size()));

        l.fork();
        r.fork();

        List<Integer> left = l.join();
        List<Integer> right = r.join();

        return merge(left, right);
    }

    private List<Integer> merge(List<Integer> left, List<Integer> right) {
        List<Integer> result = new ArrayList<>(left.size() + right.size());

        int rightIndex = 0;
        for (int i = 0; i < left.size(); i++) {
            if (rightIndex >= right.size() || left.get(i) <= right.get(rightIndex)) {
                result.add(left.get(i));
            } else {
                result.add(right.get(rightIndex++));
                i -= 1;
            }
        }

        if (rightIndex < right.size()) {
            result.addAll(right.subList(rightIndex, right.size()));
        }

        return result;
    }
    
    @Test
    public void testMerge() throws ExecutionException, InterruptedException {
        List<Integer> list = Arrays.asList(100, 200, 150, 123, 4512, 3414, 3123, 34, 5412, 34, 1234, 893, 213, 455, 6, 123, 23);
        SortTask sortTask = new SortTask(list);
        ForkJoinPool pool = ForkJoinPool.commonPool();
        List<Integer> ans = pool.invoke(sortTask);
        System.out.println(ans);
    }
}