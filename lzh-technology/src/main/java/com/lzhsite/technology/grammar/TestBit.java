package com.lzhsite.technology.grammar;

import org.junit.Test;

public class TestBit {

	@Test
	public void test1(){
		//判断是否为2的阶乘 n & (n-1) == 0
		System.out.println((8 & 7)==1);
		
		
		//判断奇偶性n & 1 == 1 奇数
		System.out.println((8 & 1) == 1);
	}
}
