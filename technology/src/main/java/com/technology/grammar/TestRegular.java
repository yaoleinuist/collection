package com.technology.grammar;

public class TestRegular {

	
	public static void main(String[] args) {
		System.out.println("pager.offset=10&pager.offset=20".replaceAll("(pager.offset=[0-9&]*)", ""));
	}
}
