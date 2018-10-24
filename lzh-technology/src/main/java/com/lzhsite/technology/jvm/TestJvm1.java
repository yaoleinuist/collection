package com.lzhsite.technology.jvm;

public class TestJvm1 {
	public static void main(String[] args) {

		//-Xms5m -Xmx20m -XX:+PrintGCDetails -XX:+UseSerialGC -XX:+PrintCommandLineFlags
		//-Xms   java初始堆 start
		//-Xmx   java最大堆 max
		//-XX:+PrintGC        遇到GC自动打印日志
		//-XX:+UseSerialGC    使用传型的垃圾收集器
		//-XX:+PrintCommandLineFlags 显式或则隐式传给虚拟机的参数输出
		
		//查看gc信息
		System.out.println("max memory:" + Runtime.getRuntime().maxMemory());
		System.out.println("free memory:" + Runtime.getRuntime().freeMemory());
		System.out.println("total memory:" + Runtime.getRuntime().totalMemory());
		
		byte[] b1 = new byte[1*1024*1024];
		System.out.println("分配了1M");
		System.out.println("max memory:" + Runtime.getRuntime().maxMemory());
		System.out.println("free memory:" + Runtime.getRuntime().freeMemory());
		System.out.println("total memory:" + Runtime.getRuntime().totalMemory());
		
		byte[] b2 = new byte[4*1024*1024];
		System.out.println("分配了4M");
		System.out.println("max memory:" + Runtime.getRuntime().maxMemory());
		System.out.println("free memory:" + Runtime.getRuntime().freeMemory());
		System.out.println("total memory:" + Runtime.getRuntime().totalMemory());
		
/*	   -XX:+PrintGCDetails 打印GC详情到控制台包括各个区的详情
 
       Heap
		 def new generation   total 1920K, used 51K [0x00000000fec00000, 0x00000000fee10000, 0x00000000ff2a0000)
		  eden space 1728K,   2% used [0x00000000fec00000, 0x00000000fec0cdb8, 0x00000000fedb0000)
		  from space 192K,   0% used [0x00000000fedb0000, 0x00000000fedb0000, 0x00000000fede0000)
		  to   space 192K,   0% used [0x00000000fede0000, 0x00000000fede0000, 0x00000000fee10000)
		 tenured generation   total 8196K, used 6063K [0x00000000ff2a0000, 0x00000000ffaa1000, 0x0000000100000000)
		   the space 8196K,  73% used [0x00000000ff2a0000, 0x00000000ff88bf38, 0x00000000ff88c000, 0x00000000ffaa1000)
		 Metaspace       used 2852K, capacity 4486K, committed 4864K, reserved 1056768K
		  class space    used 304K, capacity 386K, committed 512K, reserved 1048576K
		  
 */
	}
}
