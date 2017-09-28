package com.technology.grammar;

import org.junit.Test;

public class TestFilnally {

	public static void main(String[] args) {
		//System.out.println(test1()); // 0
		System.out.println(test2()); // 0
	}

	public static int test1() {

		int a = 0;
		try {
			return a;
		} finally {
			// TODO: handle finally clause
			a = 10;
		}

	}

	public static int test2() {

		int a = 0;
		try {
			return ++a;
		} catch (Exception e) {
			// TODO: handle exception
			return 10;
		}finally {
			// TODO: handle finally clause
			return --a;
		}

	}
	 
}
