package com.lzhsite.technology.rpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.lzhsite.technology.rpc.impl.HelloWorldServiceImpl;

/**
 *  怎么封装通信细节才能让用户像以本地调用方式调用远程服务呢？对java来说就是使用代理！java代理有两种方式：

    jdk 动态代理
         字节码生成

        尽管字节码生成方式实现的代理更为强大和高效，但代码维护不易，大部分公司实现RPC框架时还是选择动态代理方式。
     
        动态代理 
	与静态代理类对照的是动态代理类，动态代理类的字节码在程序运行时由Java反射机制动态生成，
	无需程序员手工编写它的源代码。动态代理类不仅简化了编程工作，而且提高了软件系统的可扩展性
	因为Java 反射机制可以生成任意类型的动态代理类。java.lang.reflect,
	 包中的Proxy类和InvocationHandler 接口提供了生成动态代理类的能力    
        
        
 *  @author lzh
 *
 */
 

public class TestProxy {

    public static void main(String[] args) {

          
    	  /***测试jdk代理****/
    	 /*代理类的invoke方法中封装了与远端服务通信的细节，消费方首先从RPCProxyClient获得服务提供方的接口，
    	   当执行helloWorldService.sayHello(“test”)方法之前就会先调用invoke方法。*/
    	
    	   HelloWorldService helloWorldService = new HelloWorldServiceImpl();  
    	   
           InvocationHandler handler = new JDKProxyClient(helloWorldService);  
         
           Object proxyObj = Proxy.newProxyInstance(helloWorldService.getClass().getClassLoader(),  
        		   helloWorldService.getClass().getInterfaces(), handler);  
     
           ((HelloWorldService) proxyObj).sayHello("test");
 
           
    	 /***测试cglib代理****/
    	  CglibProxy proxy = new CglibProxy();  
    	  //通过生成子类的方式创建代理类  
    	  HelloWorldService2  proxyImp = (HelloWorldService2)proxy.getProxy(HelloWorldService2.class);  
    	  proxyImp.sayHello("test");  
    	 
    	
    	

    }
	
}
