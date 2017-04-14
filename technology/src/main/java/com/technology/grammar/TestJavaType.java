package com.technology.grammar;

public class TestJavaType {

	public static void test(float f) {
        System.out.println("float");
    }
 
    public static void test(double d) {
        System.out.println("double");
    }
 
    public static void test(int i) {
        System.out.println("int");
    }
 
    public static void test(short i) {
        System.out.println("short");
    }
    public static void test(byte i) {
        System.out.println("byte");
    }
 
 
    public static void test(char i) {
        System.out.println("char");
    }
 
	
	public static void main(String[] args) {
 
		short a=1;
		
		short b=1;
/*		当short，byte，char参加运算时，结果为int型，而非与较高的类型相同。如果变量是byte，short，byte类型，
		当对其赋予编译时期的常量，而该常量又没有超过变量的取值范围时，编译器就可以进行隐式的收缩转换。
		这种隐式的收缩转换是安全的，因为该收缩转换只适用于变量的赋值，而不适用于方法调用语句，
		即不适用于方法调用时的参数传递。*/
		test(a+b);
		
/*		意char类型，这是一个无符号类型。因此，char与short或char与byte之间的转换必须显示地使用类型转换。 
		从byte到char的转换为扩展收缩转换，该转换比较特殊，即先将byte扩展转换到int，然后再收缩到char。
		
	        在整型数据间的扩展转换中，如果操作数是char类型（无符号类型），则进行无符号扩展，扩展位为0.如果操作数是byte，short
	        或int（有符号类型），则进行有符号扩展，扩展位为该变量的符号位。
	        
	        整型数据之间的收缩转换，仅仅是截断并丢弃高位，不做任何其他处理。
		*/
	}
}
