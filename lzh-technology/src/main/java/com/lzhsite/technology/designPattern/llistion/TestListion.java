package com.lzhsite.technology.designPattern.llistion;

import java.util.Enumeration;
import java.util.Vector;
/**
 * 监听器模式：事件源经过事件的封装传给监听器，当事件源触发事件后，监听器接收到事件对象可以回调事件的方法
 * @author lzh
 *
 */
public class TestListion {
	
	   DemoSource ds;     
	   
	   public TestListion(){     
	      try{     
	         ds = new DemoSource();     
	         //将监听器在事件源对象中登记：     
	         DemoListener1 listener1 = new DemoListener1();     
	         ds.addDemoListener(listener1);     
	         ds.addDemoListener(new DemoListener() {     
	            public void handleEvent(DemoEvent event) {     
	            System.out.println("Method come from 匿名类...");     
	          }     
	        });     
	       ds.notifyDemoEvent();//触发事件、通知监听器     
	     }catch(Exception ex){  
	       ex.printStackTrace();  
	       }     
	    }     
	    
	    public static void main(String args[]) {     
	           new TestListion();     
	    }     
}
//1、首要定义事件源对象（事件源相当于单击按钮事件当中的按钮对象、属于被监听者）：
class DemoSource {     
    private Vector repository = new Vector();//监听自己的监听器队列     
    public DemoSource(){}     
    public void addDemoListener(DemoListener dl) {     
           repository.addElement(dl);     
    }     
    public void notifyDemoEvent() {//通知所有的监听器     
           Enumeration enumeration = repository.elements();     
           while(enumeration.hasMoreElements()) {     
                   DemoListener dl = (DemoListener)enumeration.nextElement();     
                 dl.handleEvent(new DemoEvent(this));     
           }     
    }     
   
}
//2、其次定义事件（状态）对象（该事件对象包装了事件源对象、作为参数传递给监听器、很薄的一层包装类）：
class DemoEvent extends java.util.EventObject {     
    public DemoEvent(Object source) {     
      super(source);//source—事件源对象—如在界面上发生的点击按钮事件中的按钮     
       //所有 Event 在构造时都引用了对象 "source"，在逻辑上认为该对象是最初发生有关 Event 的对象     
    }     
    public void say() {     
           System.out.println("This is say method...");     
    }     
} 
 //3、最后定义我们的事件侦听器接口如下
interface DemoListener extends java.util.EventListener {     
    //EventListener是所有事件侦听器接口必须扩展的标记接口、因为它是无内容的标记接口、     
    //所以事件处理方法由我们自己声明如下：     
    public void handleEvent(DemoEvent dm);     
}  

//4、监听器实现类
class DemoListener1 implements DemoListener {     
       public void handleEvent(DemoEvent de) {     
              System.out.println("Inside listener1...");     
              de.say();//回调     
       }     
}   
