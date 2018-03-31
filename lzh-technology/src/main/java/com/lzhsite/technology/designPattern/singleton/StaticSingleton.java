package com.lzhsite.technology.designPattern.singleton;

public class StaticSingleton {

	private StaticSingleton(){
		System.out.println("StaticSingleton is create");
	}
	private static class SingletonHolder {
		private static StaticSingleton instance = new StaticSingleton();
	}

	public static StaticSingleton getInstance() {
		return SingletonHolder.instance;
	}
	public static void createString(){
		System.out.println("createString in Singleton");
	}
}