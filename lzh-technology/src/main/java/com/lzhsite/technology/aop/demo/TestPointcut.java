package com.lzhsite.technology.aop.demo;

import java.lang.reflect.Method;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;

public class TestPointcut implements Pointcut {

	@Override
	public ClassFilter getClassFilter() {
		return ClassFilter.TRUE;
	}

	//必须通过某种方式来匹配方法的名称来决定是否对该方法进行增强，这就是 MethodMatcher  的作用
	@Override
	public MethodMatcher getMethodMatcher() {
		return new MethodMatcher() {

			public boolean matches(Method method, Class<?> targetClass, Object[] args) {
				if (method.getName().equals("test")) {
					return true;
				}
				return false;
			}

			public boolean matches(Method method, Class<?> targetClass) {
				if (method.getName().equals("test")) {
					return true;
				}
				return false;
			}

			public boolean isRuntime() {
				return true;
			}
		};
	}
}