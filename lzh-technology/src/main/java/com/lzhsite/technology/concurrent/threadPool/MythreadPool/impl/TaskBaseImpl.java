package com.lzhsite.technology.concurrent.threadPool.MythreadPool.impl;

import com.lzhsite.technology.concurrent.threadPool.MythreadPool.inter.ITask;

public abstract class TaskBaseImpl implements ITask{
    public int maxAgainExecuteNum;

    @Override
    public abstract void stratWork() throws Exception;

    @Override
    public int getPriority() throws Exception {
        return ITask.NORM_PRIORITY;
    }

    @Override
    public int getMaxAgainExecuteNum() {
        return maxAgainExecuteNum;
    }
    
}
