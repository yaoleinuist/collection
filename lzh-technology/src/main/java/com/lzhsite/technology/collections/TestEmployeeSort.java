package com.lzhsite.technology.collections;

import java.util.Arrays;

import com.lzhsite.dto.EmployeeDto;

public class TestEmployeeSort {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EmployeeDto[] staff = new EmployeeDto[3];
		staff[0] = new EmployeeDto("harry Hacker", 35000);
		staff[1] = new EmployeeDto("carl cracke", 75000);
		staff[2] = new EmployeeDto("tony Tester", 38000);
		Arrays.sort(staff); // sort方法可以实现对对象数组排序，但是必须实现 Comparable接口
		/*
		 * Comparable接口原型为： public interface Comparable<T> { int compareTo（T
		 * other);//接口的中方法自动属于public方法 }
		 */
		for (EmployeeDto e : staff)
			System.out.println("id=" + e.getId() + "  name=" + e.getName()
					+ ".salary=" + e.getSalary());
	}
}
