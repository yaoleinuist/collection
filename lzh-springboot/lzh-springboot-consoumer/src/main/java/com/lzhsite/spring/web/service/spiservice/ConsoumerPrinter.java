package com.lzhsite.spring.web.service.spiservice;

import com.lzhsite.spring.web.service.spi.Printer;

public class ConsoumerPrinter implements Printer{

public void print(){

        System.out.println("我是消费者");

    }

}