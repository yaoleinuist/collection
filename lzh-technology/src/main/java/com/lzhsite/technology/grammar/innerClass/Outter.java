package com.lzhsite.technology.grammar.innerClass;
//https://www.cnblogs.com/hasse/p/5020519.html
public class Outter {
	private int age = 12;

	public Outter(int age) {
		this.age = age;
		getInInstance().print(); // 必须先创建成员内部类的对象，再进行访问!
	}

	private Inner getInInstance() {
		return new Inner();
	}

	class Inner {
		public void print() {
			System.out.println("内部类没同名，所以直接调用外部类成员变量：" + age);
		}
	}

	public static void main(String[] args) {
		Outter out = new Outter(10);
	}
}
