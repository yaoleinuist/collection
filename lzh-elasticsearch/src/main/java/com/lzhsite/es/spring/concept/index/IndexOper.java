package com.lzhsite.es.spring.concept.index;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class IndexOper {
	@Autowired
	private ElasticsearchTemplate et;

	// 添加索引
	public void addIndex(String indexName) {
		et.createIndex(indexName);
	}

	// 删除索引
	public void removeIndex(String indexName) {
		et.deleteIndex(indexName);
	}

	// Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
	public static void transMap2Bean(Map<String, Object> map, Object obj) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (map.containsKey(key)) {
					Object value = map.get(key);
					// 得到property对应的setter方法
					if (StringUtils.isEmpty(value)) {
						continue;
					}
					Method setter = property.getWriteMethod();
					Class<?> parameterType = setter.getParameterTypes()[0];
					if (parameterType.isAssignableFrom(Long.class) && value instanceof Number) {
						setter.invoke(obj, new Long(value.toString()));
					} else if (parameterType.isAssignableFrom(Date.class) && value instanceof Number) {
						value = new Date(Long.valueOf(value.toString()));
						setter.invoke(obj, value);
					} else if (parameterType.isAssignableFrom(BigDecimal.class) && value instanceof Number) {
						value = new BigDecimal(value.toString());
						setter.invoke(obj, value);
					} else if (parameterType.isAssignableFrom(Integer.class) && value instanceof Number) {
						value = new Integer(value.toString());
						setter.invoke(obj, value);
					} else {
						setter.invoke(obj, value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ApplicationContext atc = new ClassPathXmlApplicationContext("spring/spring-es.xml");
		IndexOper index = (IndexOper) atc.getBean("indexOper");

		String indexName = "ibeifengspring";
		// index.addIndex(indexName);
		index.removeIndex(indexName);
	}

}
