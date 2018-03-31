package com.lzhsite.technology.grammar.innerClass;
/*本例中我们将内部类移到了外部类的方法中，然后在外部类的方法中再生成一个内部类对象去调用内部类方法。如果此时我们需要往外部类的方法中传入参数，那么外部类的方法形参必须使用final定义。

换句话说，在方法中定义的内部类只能访问方法中final类型的局部变量，这是因为在方法中定义的局部变量相当于一个常量，它的生命周期超出方法运行的生命周期，由于局部变量被设置为final，所以不能再内部类中改变局部变量的值。
（这里看到网上有不同的解释，还没有彻底搞清楚==）*/
public class Outter3 {
	  private int age = 12;
      
	    public void Print(final int x) {    //这里局部变量x必须设置为final类型！
	        class Inner {
	            public void inPrint() {
	                System.out.println(x);
	                System.out.println(age);
	            }
	        }
	        new Inner().inPrint();
	    }
	      
	    public static void main(String[] args) {
	    	Outter3 out = new Outter3();
	        out.Print(10);
	    }
}
