package com.lzhsite.spring;

import java.util.ServiceLoader;
import com.lzhsite.spring.web.service.spi.Printer;
/*
       测试spi需要引入的配置列表
	lzh-spirngboot-openapi/src/main/java/com.lzhsite.spring.web.service.spi.Printer;
	lzh-springboot-consoumer/src/main/resources/META-INF/services/com.lzhsite.spring.web.service.spi.Printer
	lzh-springboot-provider/src/main/resources/META-INF/services/com.lzhsite.spring.web.service.spi.Printer

	<dependency>
	<groupId>com.lzhsite</groupId>
	<artifactId>lzh-springboot-provider</artifactId>
	<version>1.0-SNAPSHOT</version>
	</dependency>
	
	<dependency>
	<groupId>com.lzhsite</groupId>
	<artifactId>lzh-spirngboot-openapi</artifactId>
	<version>1.0-SNAPSHOT</version>
	</dependency>
*/
public class Application {

	public static void main(String[] args) {

		ServiceLoader<Printer> printerLoader = ServiceLoader.load(Printer.class);

		for (Printer printer : printerLoader) {

			printer.print();

		}
	}

}
