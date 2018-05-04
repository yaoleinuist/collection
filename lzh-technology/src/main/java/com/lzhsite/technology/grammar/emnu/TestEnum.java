package com.lzhsite.technology.grammar.emnu;

import org.junit.Test;

import com.lzhsite.core.utils.EnumUtil;

//https://blog.csdn.net/lufeng20/article/details/8730604
//http://www.hankcs.com/program/java/enum-java-examples-of-dynamic-modification.html
public class TestEnum {

	
	@Test
	public void test1(){
		DeleteStatusEnum deleteStatusEnum=EnumUtil.getByStringcode(DeleteStatusEnum.class, "getTypeCode", "0");
	    System.out.println(deleteStatusEnum.getTypeName());
	}
}
