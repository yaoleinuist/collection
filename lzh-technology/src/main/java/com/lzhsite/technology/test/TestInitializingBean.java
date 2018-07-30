package com.lzhsite.technology.test;

import java.util.Date;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Spring-Bean的初始化
 * @author lzhcode
 *
 */
public class TestInitializingBean implements InitializingBean{
	
		private String msg=null;//该变量用来存储字符串
		private Date date=null;//该变量用来存储日期
		@Override
		public void afterPropertiesSet() {
			// TODO Auto-generated method stub
			this.msg="HelloWorld";
			this.date=new Date();
		}
		
		public void init(){
			this.msg="HelloWorld";
			this.date=new Date();
		}
		
		//设定变量msg的set方法
		public void setMsg(String msg) {
			this.msg=msg;
		}
		
		//获取变量msg的get方法
		public String getMsg() {
			return this.msg;
		}
		
		public Date getDate() {
			return this.date;
		}
		
		public void setDate(Date date) {
			this.date = date;
		}
		
		public static void main(String[] args) {
			
			ApplicationContext actx=new FileSystemXmlApplicationContext("classpath:spring/spring-testBeans.xml");
			
			 //通过Bean的id来获取Bean
			TestInitializingBean initializingBean=(TestInitializingBean)actx.getBean("initializingBean");
	    	
	    	//打印输出
	    	System.out.println(initializingBean.getMsg()+" "+initializingBean.getDate());
		}

}
