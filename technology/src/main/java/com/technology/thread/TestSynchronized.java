package com.technology.thread;
/**
 * test1 先于 test2 执行 同步方法，但是却后于 test2 结束。这里并没有达到互斥的效果！
 * 原因是：MethodSync是实例变量，每次创建一个Test对象就会创建一个MethodSync对象， 
 * synchronized 只会锁定调用method()方法的那个MethodSync对象，
 * 而这里创建的两个线程分别拥有两个不同的MethodSync对象，它们调用method方法时就没有互斥关系。
 * @author Administrator
 *
 */
public class TestSynchronized implements Runnable{
    private String name;
//  private static MethodSync methodSync = new MethodSync();
  private MethodSync methodSync = new MethodSync();
  
  public TestSynchronized(String name){
      this.name = name;
  }
  
  @Override
  public void run() {
      methodSync.method(name);
  }
  
  public static void main(String[] args) {
      Thread t1 = new Thread(new TestSynchronized("test 1"));
      Thread t2 = new Thread(new TestSynchronized("test 2"));
      t1.start();
      t2.start();
  }
}
class MethodSync {
    
    /*
     * @Task : 测试 synchronized 修饰方法时锁定的是调用该方法的对象
     * @param name  线程的标记名称
     */
    public  synchronized void method(String name){
        System.out.println(name + " Start a sync method");
        try{
            Thread.sleep(300);
        }catch(InterruptedException e){}
        System.out.println(name + " End the sync method");
    }
}