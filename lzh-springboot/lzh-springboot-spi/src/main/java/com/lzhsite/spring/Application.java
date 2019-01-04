package com.lzhsite.spring;

import java.util.ServiceLoader;
import com.lzhsite.spring.web.service.spi.Printer;



public class Application {

	public static void main(String[] args) {

		ServiceLoader<Printer> printerLoader = ServiceLoader.load(Printer.class);

		for (Printer printer : printerLoader) {

			printer.print();

		}
	}

}
