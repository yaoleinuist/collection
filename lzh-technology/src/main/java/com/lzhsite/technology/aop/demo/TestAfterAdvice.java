package com.lzhsite.technology.aop.demo;

import java.lang.reflect.Method;
import org.springframework.aop.AfterReturningAdvice;

public class TestAfterAdvice implements AfterReturningAdvice {

	@Override
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		System.out.println("after " + target.getClass().getSimpleName() + "." + method.getName() + "()");
	}
}