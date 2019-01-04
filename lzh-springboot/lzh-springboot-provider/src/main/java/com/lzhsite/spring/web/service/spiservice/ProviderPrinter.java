package com.lzhsite.spring.web.service.spiservice;

import com.lzhsite.spring.web.service.spi.Printer;

public class ProviderPrinter implements Printer{

public void print(){

        System.out.println("我是提供者");

    }
 
}