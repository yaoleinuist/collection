package com.lzhsite.entity;

import java.util.Random;

public class Employee implements Comparable<Employee> {
	public Employee(String n, double s) {
		name = n;
		salary = s;
		Random ID = new Random();
		id = ID.nextInt(10000000);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getSalary() {
		return salary;
	}

	public void raiseSalary(double byPercent) {
		double raise = salary * byPercent / 100;
		salary += raise;
	}

	public int compareTo(Employee other) {
		if (id < other.id) // 这里比较的是什么 sort方法实现的就是按照此比较的东西从小到大排列
			return -1;
		if (id > other.id)
			return 1;
		return 0;
	}

	private int id;
	private String name;
	private double salary;
}