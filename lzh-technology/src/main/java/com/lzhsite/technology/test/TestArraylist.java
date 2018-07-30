package com.lzhsite.technology.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.lzhsite.technology.grammar.lambda.User;

public class TestArraylist {

	/**
	 * list里循环放入同一个对象的测试
	 * 结果：
	 * 9 9 9 9 9 9 9 9 9 9 
	 */
	@Test
	public void test1(){
		User user=new User();
		List<User> list=new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			user.setAge(i);
			list.add(user);
		}
		for (int i = 0; i < 10; i++) {
			System.out.print(list.get(i).getAge()+" ");
		}
	}
}
