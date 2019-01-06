package com.lzhsite.technology.grammar;

import org.junit.Test;

public class TestByte {

	@Test
	public void test1(){
		//判断是否为2的阶乘 n & (n-1) == 0
		System.out.println((8 & 7)==1);
		
		
		//判断奇偶性n & 1 == 1 奇数
		System.out.println((8 & 1) == 1);
	}
	
	
	/**
	 * byte 8 bits -128 - + 127 
	 * 1 bit = 1 二进制数据 
	 * 1 byte = 8 bit 
	 * 1 字母 = 1 byte = 8 bit(位) 
	 * 1 汉字 = 2 byte = 16 bit
	 */
	
	@Test
	public void test2(){
		// TODO Auto-generated method stub
		byte b1 = 127;
		byte b2 = -128;
		byte b3 = 'a';
		byte b4 = 'A'; // 一个字母 = 1 byte = 8 bit
		// byte b5 ='aa'; 这就错了
		// byte b6 ='中'; 这就错了 一个汉字 2个字节 16bit
		short s1 = '啊'; // 一个汉字 2个字节 16bit short 是 16 bit位的
		// short s2 = '汉字'; // 2个汉字 4个字节 32 bit int 是32 bit的
		// int i1 = '汉字'; 但是 int 是数字类型的 , char 是 16 bit的 = 2 byte = 一个汉字
		char c1 = '汗';
		// byte 转换 string
		String string = "中文";
		byte by[] = string.getBytes();
		String str = new String(by);
		System.out.println("str=" + str);
	}
	

}
