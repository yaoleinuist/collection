package com.lzhsite.technology.rpc;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
/**
 * JDK的动态代理依靠接口实现，如果有些类并没有实现接口，则不能使用JDK代理，
 * @author lzh
 *
 */
public class JDKProxyClient implements java.lang.reflect.InvocationHandler{

    private Object obj;

 

    public JDKProxyClient(Object obj){

        this.obj=obj;

    }

 

    /**

     * 得到被代理对象;

     */

    public static Object getProxy(Object obj){

        return Proxy.newProxyInstance(obj.getClass().getClassLoader(),

                obj.getClass().getInterfaces(), new JDKProxyClient(obj));

    }

 

    /**

     * 调用此方法执行

     */

    public Object invoke(Object proxy, Method method, Object[] args)

            throws Throwable {

    	  System.out.println("INFO: " + obj.getClass().getName() + " staring...");  
          Object ob =  method.invoke(obj, args);  
          System.out.println("INFO: " + obj.getClass().getName() + " ending...");  
          return ob;  
 

    }

}