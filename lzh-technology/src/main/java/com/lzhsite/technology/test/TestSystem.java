package com.lzhsite.technology.test;

import java.util.Map.Entry;

import org.junit.Test;

public class TestSystem {
	
	/**
	 * 获取系统环境变量
	 */
	@Test
	public void output(){
		System.out.println("output environment var:");
		for(Entry<String, String> entry:System.getenv().entrySet()){
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}

}
