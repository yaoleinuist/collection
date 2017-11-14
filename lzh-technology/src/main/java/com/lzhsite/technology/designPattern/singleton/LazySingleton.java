package com.lzhsite.technology.designPattern.singleton;
//单例模式的线程安全意味着：某个类的实例在多线程环境下只会被创建一次出来
public class LazySingleton {
	//懒汉式单例模式
    //比较懒，在类加载时，不创建实例，因此类加载速度快，但运行时获取对象的速度慢
    private static LazySingleton intance = null;//静态私用成员，没有初始化
    
    private LazySingleton()
    {
        //私有构造函数
    }
    
    public static synchronized LazySingleton getInstance()    //静态，同步，公开访问点
    {
        if(intance == null)
        {
            intance = new LazySingleton();
            System.out.println("new LazySingleton");
        }
        return intance;
    }
    
    public static void main(String[] args) {
       
    	LazySingleton.getInstance();
        Thread t1 = new Thread(new TestLazySingleton("test0"));
        Thread t2 = new Thread(new TestLazySingleton("test1"));
        t1.start();
        t2.start();
    }
}
 
class TestLazySingleton implements Runnable{

private String name;

public TestLazySingleton(String name) {
	// TODO Auto-generated constructor stub
  this.name=name;
}

@Override
public void run() {
  System.out.print(Thread.currentThread().getName()+" "+name+"  ");
  LazySingleton.getInstance();
}


}