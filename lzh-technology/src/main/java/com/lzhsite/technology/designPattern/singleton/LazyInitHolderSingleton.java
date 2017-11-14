package com.lzhsite.technology.designPattern.singleton;

import java.lang.reflect.Constructor;

/**
 * 提高懒汉单例模式的效率
 * @author lzh
 *
 */
public class LazyInitHolderSingleton {

	private static boolean flag = false;  
	
	private LazyInitHolderSingleton(){
		   //防止单例模式被JAVA反射攻击
		   synchronized(LazyInitHolderSingleton.class)  
	        {  
	            if(flag == false)  
	            {  
	                flag = !flag;  
	            }  
	            else 
	            {  
	                throw new RuntimeException("单例模式被侵犯！");  
	            }  
	        } 
	}
	
	private static class SingletonHolder {    
		private	static final LazyInitHolderSingleton INSTANCE = new LazyInitHolderSingleton();
	}
	
	
	public static  LazyInitHolderSingleton getInstance(){
		
		return SingletonHolder.INSTANCE;
	}
	
    public static void main(String[] args)  
    {  
        try 
        {  
            Class<LazyInitHolderSingleton> classType = LazyInitHolderSingleton.class;  
 
            Constructor<LazyInitHolderSingleton> c = classType.getDeclaredConstructor(null);  
            c.setAccessible(true);  
            LazyInitHolderSingleton e1 = (LazyInitHolderSingleton)c.newInstance();  
            LazyInitHolderSingleton e2 = LazyInitHolderSingleton.getInstance();  
            System.out.println(e1==e2);  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }  
	
}
