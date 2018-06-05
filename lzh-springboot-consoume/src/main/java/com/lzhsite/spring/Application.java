package com.lzhsite.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

//spring多个AOP执行先后顺序
//https://blog.csdn.net/qqXHwwqwq/article/details/51678595
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) 
@EnableAutoConfiguration
public class Application  implements CommandLineRunner {


	private static final Logger log = LoggerFactory.getLogger(Application.class);

	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {
		log.info("消费者启动完毕------>>启动完毕");
	}
}
