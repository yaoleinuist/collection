package com.technology.designPattern.proxy.hibernate;

// Generated 2012-2-14 15:38:26 by Hibernate Tools 3.4.0.CR1

/**
 * User generated by hbm2java
 */
public class User implements java.io.Serializable {

	private Integer id;
	private String name;
	private int age;

	public User() {
	}

	public User(String name, int age) {
		this.name = name;
		this.age = age;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return this.age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
