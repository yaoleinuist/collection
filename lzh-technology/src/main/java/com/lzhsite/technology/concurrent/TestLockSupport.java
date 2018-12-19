package com.lzhsite.technology.concurrent;

import java.util.concurrent.locks.LockSupport;
/**
 * LockSupport比Object的wait/notify有两大优势：
 * 1.LockSupport不需要在同步代码块里 。所以线程间也不需要维护一个共享的同步对象了，实现了线程间的解耦。
 * 2.unpark函数可以先于park调用，所以不需要担心线程间的执行的先后顺序。
 * @author lzhcode
 *
 */
public class TestLockSupport {
	
	public static void main(String[] args)throws Exception {
        final Object obj = new Object();
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for(int i=0;i<10;i++){
                    sum+=i;
                }
                LockSupport.park();
                System.out.println(sum);
            }
        });
        A.start();
        //睡眠一秒钟，保证线程A已经计算完成，阻塞在wait方法
        //Thread.sleep(1000);
        LockSupport.unpark(A);
    }
}
