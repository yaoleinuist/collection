package com.lzhsite.technology.aop;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class Aspect2 {

    @Before(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
    public void before(JoinPoint joinPoint) {
        System.out.println("[Aspect2] before advise");
    }

    @Around(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
    public void around(ProceedingJoinPoint pjp) throws Throwable{
        System.out.println("[Aspect2] around advise 1");
        pjp.proceed();
        System.out.println("[Aspect2] around advise2");
    }

    @AfterReturning(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
    public void afterReturning(JoinPoint joinPoint) {
        System.out.println("[Aspect2] afterReturning advise");
    }

    @AfterThrowing(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
    public void afterThrowing(JoinPoint joinPoint) {
        System.out.println("[Aspect2] afterThrowing advise");
    }

    @After(value = "com.lzhsite.technology.aop.PointCuts.aopDemo()")
    public void after(JoinPoint joinPoint) {
        System.out.println("[Aspect2] after advise");
    }
}