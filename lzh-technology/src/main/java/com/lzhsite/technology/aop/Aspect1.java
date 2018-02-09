package com.lzhsite.technology.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;


/**
 * http://blog.csdn.net/rainbow702/article/details/52185827
 * 
 * 只有一个Aspect时执行顺序Around->Before->Method->Around->After->AfterReturning(AfterThrowing)
 * 注意点
 * 
 * 1如果在同一个 aspect 类中，针对同一个 pointcut，定义了两个相同的 advice(比如，定义了两个 @Before)，
 * 那么这两个 advice 的执行顺序是无法确定的，哪怕你给这两个 advice 添加了 @Order 这个注解，也不行。这点切记。
 * 
 * 2对于@Around这个advice，不管它有没有返回值，但是必须要方法内部，调用一下 pjp.proceed();
 * 否则，Controller 中的接口将没有机会被执行，从而也导致了 @Before这个advice不会被触发。
 * 比如，我们假设正常情况下，执行顺序为”aspect2 -> apsect1 -> controller”，
 * 如果，我们把 aspect1中的@Around中的 pjp.proceed();给删掉，那么，我们看到的输出结果将是：
 * @author lzhcode
 *
 */


@Component
@Aspect
public class Aspect1 {
	   @Before(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
	    public void before(JoinPoint joinPoint) {
	        System.out.println("[Aspect1] before advise");
	    }

	    @Around(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
	    public void around(ProceedingJoinPoint pjp) throws  Throwable{
	        System.out.println("[Aspect1] around advise 1");
	        pjp.proceed();
	        System.out.println("[Aspect1] around advise2");
	    }

	    @AfterReturning(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
	    public void afterReturning(JoinPoint joinPoint) {
	        System.out.println("[Aspect1] afterReturning advise");
	    }

	    @AfterThrowing(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
	    public void afterThrowing(JoinPoint joinPoint) {
	        System.out.println("[Aspect1] afterThrowing advise");
	    }

	    @After(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
	    public void after(JoinPoint joinPoint) {
	        System.out.println("[Aspect1] after advise");
	    }
}
