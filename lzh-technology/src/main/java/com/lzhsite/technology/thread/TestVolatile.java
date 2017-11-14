package com.lzhsite.technology.thread;

public class TestVolatile {
	
	private volatile static int i = 0;
	
	public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread() {
            @Override
            public void run() {
                for( int j = 0 ; j < 1000000 ; j++ ){
                	i++;
                	//System.out.println("a");
                }
            }
        };
        a.start();
        Thread b = new Thread() {
            @Override
            public void run() {
                for( int j = 0 ; j < 1000000 ; j++ ){
                	i--;
                  	//System.out.println("b");
                }
            }
        };
        b.start();
        //thread.Join把指定的线程加入到当前线程，可以将两个交替执行的线程合并为顺序执行的线程。
        //比如在线程B中调用了线程A的Join()方法，直到线程A执行完毕后，才会继续执行线程B。
        a.join();
        b.join();
        System.out.println(i);
    }
}
