package com.lzhsite.technology.concurrent.framwork.forkjoin.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

/**
 * Created by yihui on 2018/4/8.
 */
public class DefaultForkJoinDataLoader<T> extends AbstractDataLoader<T> {
    /**
     * 待执行的任务列表
     */
    private List<AbstractDataLoader> taskList;


    public DefaultForkJoinDataLoader(T context) {
        super(context);
        taskList = new ArrayList<>();
    }


    public DefaultForkJoinDataLoader<T> addTask(IDataLoader dataLoader) {
        taskList.add(new AbstractDataLoader(this.context) {
            @Override
            public void load(Object context) {
                dataLoader.load(context);
            }
        });
        return this;
    }


    @Override
    public void load(Object context) {
        this.taskList.forEach(ForkJoinTask::fork);
    }


    /**
     * 获取执行后的结果
     * @return
     */
    public T getContext() {
        this.taskList.forEach(ForkJoinTask::join);
        return this.context;
    }
}
