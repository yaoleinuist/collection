package com.technology.collections;

import java.util.Arrays;

import com.entity.Employee;

public class EmployeeSortTest {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Employee[] staff = new Employee[3];
		staff[0] = new Employee("harry Hacker", 35000);
		staff[1] = new Employee("carl cracke", 75000);
		staff[2] = new Employee("tony Tester", 38000);
		Arrays.sort(staff); // sort方法可以实现对对象数组排序，但是必须实现 Comparable接口
		/*
		 * Comparable接口原型为： public interface Comparable<T> { int compareTo（T
		 * other);//接口的中方法自动属于public方法 }
		 */
		for (Employee e : staff)
			System.out.println("id=" + e.getId() + "  name=" + e.getName()
					+ ".salary=" + e.getSalary());
	}
}
