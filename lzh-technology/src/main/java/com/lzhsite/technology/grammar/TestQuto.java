package com.lzhsite.technology.grammar;

import com.lzhsite.entity.User;

public class TestQuto {

	public static void main(String[] args) {
		User user=new User();
		setId(user);
		System.out.println(user.getId());
		
		int a=2;
		change(a);
		System.out.println(a);
		System.out.println(tryException());
		
	}
	
	
	private static void change(int a) {
		// TODO Auto-generated method stub
		a=3;
	}


	public static int tryException(){
		
		int a=1;
		try {
			return a;
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			a++;
		}
		return a;
		
	}
	
	public static void setId(User user) {
		//user=new User();   注意有加这句和没加这句打印结果的区别
		user.setId(1);
	}
	
}
