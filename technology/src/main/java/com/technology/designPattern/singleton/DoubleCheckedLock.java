package com.technology.designPattern.singleton;

 
import com.technology.thread.TestSynchronized;

/*不可否认，synchronized关键字是可以保证单例，但是程序的性能却不容乐观，
原因在于getInstance()整个方法体都是同步的，这就限定了访问速度。
其实我们需要的仅仅是在首次初始化对象的时候需要同步，
对于之后的获取不需要同步锁。因此，可以做进一步的改进：*/

public class DoubleCheckedLock {  
    private static DoubleCheckedLock instance;    
        
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