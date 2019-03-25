package com.lzhsite.technology.designPattern.singleton;

 

/**
 * 不可否认，synchronized关键字是可以保证单例，但是程序的性能却不容乐观，
 * 原因在于getInstance()整个方法体都是同步的，这就限定了访问速度。
 * 其实我们需要的仅仅是在首次初始化对象的时候需要同步，
 * 对于之后的获取不需要同步锁。因此，可以做进一步的改进：
 */

public class DoubleCheckedLock {  
	
	/**
	 * 另外，需要注意 instance 采用 volatile 关键字修饰也是很有必要。
     * instance 采用 volatile 关键字修饰也是很有必要的， instance = new DoubleCheckedLock(); 这段代码其实是分为三步执行：
     * 为 instance 分配内存空间
     * 初始化 instance
     * 将 instance 指向分配的内存地址
     * 但是由于 JVM 具有指令重排的特性，执行顺序有可能变成 1->3->2。指令重排在单线程环境下不会出先问题，
     * 但是在多线程环境下会导致一个线程获得还没有初始化的实例。例如，线程 T1 执行了 1 和 3，此时 T2 
     * 调用 getUniqueInstance() 后发现 uniqueInstance 不为空，因此返回 instance，但此时 instance 还未被初始化。
     * 使用 volatile 可以禁止 JVM 的指令重排，保证在多线程环境下也能正常运行。
	 */
    private volatile  static DoubleCheckedLock instance;    
        
    public static DoubleCheckedLock getInstance() {    
        if (instance == null) {  //step1  
            synchronized (DoubleCheckedLock.class) { //step2  
            	 if (instance == null) {  //step3 
                	System.out.println("new DoubleCheckedLock");
                    instance=new DoubleCheckedLock(); //step4  
            	 }
            }  
        }    
        return instance;    
    }    
    
    public static void main(String[] args) {
        Thread t1 = new Thread(new TestDoubleCheckedLock("test0"));
        Thread t2 = new Thread(new TestDoubleCheckedLock("test1"));
        t1.start();
        t2.start();
    }
} 

class TestDoubleCheckedLock implements Runnable{
 
  private String name;
	
  public TestDoubleCheckedLock(String name) {
		// TODO Auto-generated constructor stub
	  this.name=name;
	}

  @Override
  public void run() {
      System.out.print(Thread.currentThread().getName()+" "+name+"  ");
	  DoubleCheckedLock.getInstance();
  }
  

}