package com.lzhsite.technology.jvm;

public class TestJvm2 {
	
	public static void main(String[] args) {
		
		//-XX:SurvivorRatio 设置新生代代空间eden和s1/s2的比例大小 (eden/s1=eden/s2=-XX:SurvivorRatio)
		//-Xmn  新生代初始大小 new 
		
		//第一次配置
		//-Xms20m -Xmx20m -Xmn1m -XX:SurvivorRatio=2 -XX:+PrintGCDetails -XX:+UseSerialGC
		
		//第二次配置
		//-Xms20m -Xmx20m -Xmn7m -XX:SurvivorRatio=2 -XX:+PrintGCDetails -XX:+UseSerialGC
		
		//第三次配置
		//-XX:NewRatio=老年代/新生代
		//-Xms20m -Xmx20m -XX:SurvivorRatio=2 -XX:+PrintGCDetails -XX:+UseSerialGC
		
		byte[] b = null;
		//连续向系统申请10MB空间
		for(int i = 0 ; i <10; i ++){
			b = new byte[1*1024*1024];
		}
	}
}
