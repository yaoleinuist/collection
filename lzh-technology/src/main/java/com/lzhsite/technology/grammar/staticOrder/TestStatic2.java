package com.lzhsite.technology.grammar.staticOrder;

public class TestStatic2 {
	public static void main(String[] args) {
		staticFunction();
	}

	static TestStatic2 st = new TestStatic2();

	static {
		System.out.println("1");
	}

	{
		System.out.println("2");
	}

	TestStatic2()
	   {
	       System.out.println("3");
	       System.out.println("a="+a+",b="+b);
	   }

	public static void staticFunction() {
		System.out.println("4");
	}

	int a = 110;
	static int b = 112;
}
