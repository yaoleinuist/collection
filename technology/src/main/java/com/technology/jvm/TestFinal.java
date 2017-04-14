package com.technology.jvm;

/*
final关键字提高了性能。JVM和Java应用都会缓存final变量。
final变量可以安全的在多线程环境下进行共享，而不需要额外的同步开销。
使用final关键字，JVM会对方法、变量及类进行优化。
*/

import java.util.Random;

public class TestFinal {
	static{  
        System.out.println("FinalTest static block");  
    }  
    public static void main(String[] args) {  
        System.out.println("==========================");  
        System.out.println(TestAA.xx);  
        System.out.println("==========================");  
        System.out.println(TestBB.xx);  
        System.out.println("==========================");  
        System.out.println(Child.aa); //这里实际只对父类进行了主动调用  
        Child.doSomething(); //在这个过程中，根本就没有对子类进行主动调用  
    }  
}  
  
  
/** 
 * 编译时就能计算出来"6/3"结果是2，也就是说xx是编译时的常量 
 * 所以FinalTest类在调用这里的xx时候，不会导致该类被初始化 
 * 注：这里所谓的不对类进行初始化和对类进行初始化，的根本在于 
 * 注：这里的静态代码块，不被执行和被执行 
 */  
class TestAA{  
    public static final int xx = 6 / 3;  
    static{  
        System.out.println("TestAA static block");  
    }  
}  
  
  
/** 
 * 只有在运行时才能确定随机数，故这里的xx不是编译时的常量 
 * 这时xx就是一个变量，即只有在运行的时候，才能确定它的值 
 * 所以FinalTest类在调用这里的xx时候，会导致该类初始化 
 */  
class TestBB{  
    public static final int xx = new Random().nextInt(100); //0-99之间的随机数  
    static{  
        System.out.println("TestBB static block");  
    }  
}  
  
  
class Parent{  
    static int aa = 6;  
    static{  
        System.out.println("Parent static block");  
    }  
    static void doSomething(){  
        System.out.println("Parent do Something");  
    }  
}  
class Child extends Parent{  
    static{  
        System.out.println("Child static block");  
    }  
}  