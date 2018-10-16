package com.lzhsite.technology.aop.demo;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;

/**
 *  通知器把对目标方法的增强接口 Advice 和 如何匹配目标方法接口 PointCut 结合起来
 *  
 */
public class TestAdvisor implements PointcutAdvisor {

	/**
	 * 获取通知处理逻辑
	 */
	@Override
	public Advice getAdvice() {
		return new TestAfterAdvice();
	}

	@Override
	public boolean isPerInstance() {
		return false;
	}

	/**
	 * 获取切入点
	 */
	@Override
	public Pointcut getPointcut() {
		return new TestPointcut();
	}
}