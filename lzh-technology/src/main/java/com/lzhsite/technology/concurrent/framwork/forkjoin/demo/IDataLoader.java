package com.lzhsite.technology.concurrent.framwork.forkjoin.demo;

/**
 * Created by yihui on 2018/4/8.
 */
public interface IDataLoader<T> {


    /**
     * 具体的业务逻辑，放在这个方法里面执行，将返回的结果，封装到context内
     *
     * @param context
     */
    void load(T context);

}
