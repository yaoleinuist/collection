package com.technology.rpc.impl;

import com.technology.rpc.HelloWorldService;

public class HelloWorldServiceImpl implements HelloWorldService{

	@Override
	public void sayHello(String temp) {
		// TODO Auto-generated method stub
		System.out.println(temp);
	}

}
