package com.lzhsite.technology.grammar.innerClass;

//从本例可以看出：成员内部类，就是作为外部类的成员，可以直接使用外部类的所有成员和方法，
//即使是private的。虽然成员内部类可以无条件地访问外部类的成员，而外部类想访问成员内部类的成员却不是这么随心所欲了。
//在外部类中如果要访问成员内部类的成员，必须先创建一个成员内部类的对象，再通过指向这个对象的引用来访问：

public  class Outter2 {
    private int age = 12;
    public Outter2(int age) {
        this.age = age;
        getInInstance().print();   //必须先创建成员内部类的对象，再进行访问!
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
    	Outter2 out = new Outter2(10);
    }
    
}
  
 