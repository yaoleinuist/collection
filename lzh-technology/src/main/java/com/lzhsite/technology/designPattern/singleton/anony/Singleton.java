package com.lzhsite.technology.designPattern.singleton.anony;

public abstract class Singleton {
	private Singleton(){
		 
	}

	private static Singleton instance = new Singleton(){};
	public static Singleton getInstance() {
		return instance;
	} 
}