package com.lzhsite.technology.grammar;

public class TestStatic {

	public static void main(String[] args) {
		new Child();
    }
}


class Foo {
    public Foo(String word) {
        System.out.println(word);
    }
}

class Parent {
    static Foo FOO = new Foo("Parent's static parameter");
    
    Foo foo = new Foo("Parent's parameter");
    
    static {
        System.out.println("Parent's static code block");
    }
    
    {
        System.out.println("Parent's code block");
    }
    
    public Parent() {
        System.out.println("Parent.Parent()");
    }
}

class Child extends Parent{
    static Foo FOO = new Foo("Child's static parameter");
    
    Foo foo = new Foo("Child's parameter");
    
    static {
        System.out.println("Child's static code block");
    }
    
    {
        System.out.println("Child's code block");
    }
    
    public Child() {
        System.out.println("Child.Child()");
    }
}
/*
Parent's static parameter
Parent's static code block
Child's static parameter
Child's static code block
Parent's parameter
Parent's code block
Parent.Parent()
Child's parameter
Child's code block
Child.Child() 

Java中的静态变量和静态代码块是在类加载的时候就执行的，实例化对象时，先声明并实例化变量再执行构造函数。如果子类继承父类，则先执行父类的静态变量和静态代码块，再执行子类的静态变量和静态代码块。同样，接着在执行父类和子类非静态代码块和构造函数。

注意：（静态）变量和（静态）代码块的也是有执行顺序的，与代码书写的顺序一致。在（静态）代码块中可以使用（静态）变量，但是被使用的（静态）变量必须在（静态）代码块前面声明。

最后给出执行步骤：

1、父类静态变量和静态代码块（先声明的先执行）；

2、子类静态变量和静态代码块（先声明的先执行）；

3、父类的变量和代码块（先声明的先执行）；

4、父类的构造函数；

5、子类的变量和代码块（先声明的先执行）；

6、子类的构造函数。*/
 