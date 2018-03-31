package com.lzhsite.technology.designPattern.singleton;

public class Singleton {
	private Singleton() {
		//创建单例的过程可能会比较慢
		System.out.println("Singleton is create");
	}

	private static Singleton instance = new Singleton();
	public static Singleton getInstance() {
		return instance;
	}

	public static void createString(){
		System.out.println("createString in Singleton");
	}
}