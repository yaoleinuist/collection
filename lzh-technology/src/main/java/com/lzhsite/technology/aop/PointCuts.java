package com.lzhsite.technology.aop;

import org.aspectj.lang.annotation.Pointcut;



public class PointCuts {
	
	/**
	 * Pointcut 是指那些方法需要被执行"AOP",是由"Pointcut Expression"来描述的.
	 * (https://www.cnblogs.com/rainy-shurun/p/5195439.html)
	 * 
	 * 该pointcut用来拦截AopTestController中的所有方法。
	 */
	@Pointcut(value = "within(com.lzhsite.controller.AopTestController.*(..)")
    public void aopDemo() {

    }
}
