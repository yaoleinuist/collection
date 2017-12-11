package com.lzhsite.technology.redis.redislockframework;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheLock {
	String lockedPrefix() default "";// redis 锁key的前缀

	long timeOut() default 3;// 锁时间,3s

	int expireTime() default 3;// key在redis里存在的时间，3S
}
