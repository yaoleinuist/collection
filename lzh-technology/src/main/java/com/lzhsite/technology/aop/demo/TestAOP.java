package com.lzhsite.technology.aop.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class TestAOP {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext(
				"classpath:spring/spring-testBeans.xml");
		TestTarget target = (TestTarget) applicationContext.getBean("testAOP");
		target.test();
		System.out.println("----------------");
		target.test2();
	}
}