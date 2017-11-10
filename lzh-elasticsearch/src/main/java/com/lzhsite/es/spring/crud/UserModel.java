package com.lzhsite.es.spring.crud;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName="ibeifeng-java",type="javaapi")
public class UserModel {
	@Id
	private String uuid;
	private String name;
	private int age;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "UserModel [uuid=" + uuid + ", name=" + name + ", age=" + age + "]";
	}
	
	
	
}
