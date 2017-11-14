package com.lzhsite.technology.test;

import redis.clients.jedis.Jedis;
public class TestRedis {
 
   static String constr = "114.55.7.104";  
   //static String constr = "localhost";  
   public static Jedis getRedis(){  
        Jedis jedis = new Jedis(constr,6379,5000);  
 
      return jedis;  
   }  
   
   public static void main(String[] args){  
        Jedis j = TestRedis. getRedis();  
        String output;  
        j.set( "hello", "world" );  
        output = j.get( "hello");  
        System. out.println(output);  
  }  
}