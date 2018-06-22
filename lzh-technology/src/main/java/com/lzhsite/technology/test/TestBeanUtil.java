package com.lzhsite.technology.test;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import com.lzhsite.entity.User;

public class TestBeanUtil {

	@Test
	public void test1() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		User user = new User();
		user.setName("name1");
		user.setCity(2);
		Map<String, String> map = null;
		{
			map = BeanUtils.describe(user);
		}
	}

	// 2、map 转换成 bean
	/**
	 * 
	 * 
	 * Map转换层Bean，使用泛型免去了类型转换的麻烦。
	 * 
	 * @param <T>
	 * @param map
	 * @param class1
	 * @return
	 */
	public static <T> T map2Bean(Map<String, String> map, Class<T> class1) {
		T bean = null;
		try {
			bean = class1.newInstance();
			BeanUtils.populate(bean, map);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return bean;
	}

}
