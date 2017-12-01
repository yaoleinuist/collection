package com.lzhsite.generator;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lzhsite.generator.service.GeneratorService;

/**
 * wemall自动生成工具
 */

public class WemallGeneratorApplication {

    public static void main(String[] args){
        args = new String[] {
                "classpath:spring/spring-content.xml",
        };
        String path = WemallGeneratorApplication.class.getClassLoader().getResource("").getFile()+ "../../src/main/resources";
        DOMConfigurator.configure(path+"/log4j.xml");
        ApplicationContext actx = new ClassPathXmlApplicationContext(args);
        GeneratorService generatorService = (GeneratorService) actx.getBean("generatorService");
        generatorService.generatorCode("u_user");
    }

}
