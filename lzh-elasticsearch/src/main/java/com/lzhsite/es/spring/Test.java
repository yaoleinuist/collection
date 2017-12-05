package com.lzhsite.es.spring;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lzhsite.es.spring.concept.crud.CRUDTest;
import com.lzhsite.es.spring.model.UserModel;

//http://blog.csdn.net/liyantianmin/article/details/51801961
public class Test {

	public static void main(String[] args) throws Exception {
	
		ApplicationContext atc = new ClassPathXmlApplicationContext("applicationContext-es.xml");
		CRUDTest crud = (CRUDTest)atc.getBean("CRUDTest");
		
		UserModel um = new UserModel();
		um.setUuid("ibeifeng03");
		um.setName("我的笔记本2");
		um.setAge(30);
		
		// 新增操作
 		crud.addModel(um);
 		//crud.updateModel(um);
 		//crud.deleteModel(um.getUuid());
		crud.queryUser();
		

	}

}
