package com.lzhsite.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzhsite.core.exception.OverLimitException;
import com.lzhsite.core.utils.MD5Util;
import com.lzhsite.technology.redis.limit.RateLimiter;

/**
 * Created by hao.g on 18/5/17.
 */
@Aspect
@Component
public class RateLimitAspect {
    @Autowired
    private RateLimiter rateLimiter;

    @Before("@annotation(com.lzhsite.technology.redis.limit.RateLimit)")
    public void before(JoinPoint pjp) throws Throwable {
        Signature sig = pjp.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用在方法上");
        }
        MethodSignature msig = (MethodSignature) sig;
        String methodName = pjp.getTarget().getClass().getName() + "." + msig.getName();
        String limitKey = MD5Util.md5(methodName);

        if (rateLimiter.limit(limitKey) != 1){
            throw new OverLimitException("触发限流控制");
        }
    }
}
