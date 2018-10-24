package com.lzhsite.technology.jvm;

import java.util.Vector;

public class TestJvm03 {

	//-XX:+HeapDumpOnOutOfMemoryError 设置导出堆的路径
	//memory analyzer 内存分析工具
	public static void main(String[] args) {
		
		//-Xms5m -Xmx5m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=d:/TestJvm03.dump
		//堆内存溢出
		Vector v = new Vector();
		for(int i=0; i < 5; i ++){
			v.add(new Byte[1*1024*1024]);
		}
		
	}
}
