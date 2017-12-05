package com.lzhsite.context;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext.
 * 
 */
@Service
@Lazy(false)
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	private static ApplicationContext applicationContext = null;

	private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clearHolder() {
		logger.debug("清除SpringContextHolder中的ApplicationContext:" + applicationContext);
		applicationContext = null;
	}

	/**
	 * 实现ApplicationContextAware接口, 注入Context到静态变量中.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		// logger.debug("注入ApplicationContext到SpringContextHolder:{}",
		// applicationContext);

		if (SpringContextHolder.applicationContext != null) {
			logger.warn("SpringContextHolder中的ApplicationContext被覆盖, 原有ApplicationContext为:"
					+ SpringContextHolder.applicationContext);
		}

		SpringContextHolder.applicationContext = applicationContext; // NOSONAR
	}

	/**
	 * 实现DisposableBean接口, 在Context关闭时清理静态变量.
	 */
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertContextInjected() {
		Validate.validState(applicationContext != null,
				"applicaitonContext属性未注入, 请在applicationContext.xml中定义SpringContextHolder.");
	}
	

	  /**
	  * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true 
	  * @param name
	  * @return boolean
	  */
	  public static boolean containsBean(String name) {
	    return applicationContext.containsBean(name);
	  }
	 
	  /**
	  * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
	  * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）   
	  * @param name
	  * @return boolean
	  * @throws NoSuchBeanDefinitionException
	  */
	  public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
	    return applicationContext.isSingleton(name);
	  }
	 
	  /**
	  * @param name
	  * @return Class 注册对象的类型
	  * @throws NoSuchBeanDefinitionException
	  */
	  public static Class getType(String name) throws NoSuchBeanDefinitionException {
	    return applicationContext.getType(name);
	  }
	 
	  /**
	  * 如果给定的bean名字在bean定义中有别名，则返回这些别名   
	  * @param name
	  * @return
	  * @throws NoSuchBeanDefinitionException
	  */
	  public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
	    return applicationContext.getAliases(name);
	  }
}