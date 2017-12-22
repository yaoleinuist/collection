package com.lzhsite.core.datasource;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class DataSourceAspect {
	public void before(JoinPoint point) {
		Object target = point.getTarget();
		String method = point.getSignature().getName();

		Class<?>[] classz = target.getClass().getInterfaces();

		Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
		try {
			Method m = classz[0].getMethod(method, parameterTypes);
			if (m != null && m.isAnnotationPresent(DataSource.class)) {
				DataSource data = m.getAnnotation(DataSource.class);
				if("slave".equals(data.value())){
					DataSourceHolder.setSlave();
				}else if("master".equals(data.value())){
					DataSourceHolder.setMaster();
				}else{
					DataSourceHolder.setMaster();
				}
				 
				System.out.println(data.value());
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
