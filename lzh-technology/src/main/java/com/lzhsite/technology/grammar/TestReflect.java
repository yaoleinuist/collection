package com.lzhsite.technology.grammar;

import org.junit.Test;

public class TestReflect {

	/**
	 * 获取当前运行方法所在的类和方法名
	 */
	public void showClassAndMethod() {
		System.out.println(this.getClass().getSimpleName() + ":" + new Exception().getStackTrace()[0].getMethodName());
	}

	@Test
	public void test() {
		showClassAndMethod();
	}

}
