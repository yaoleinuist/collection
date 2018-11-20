package com.lzhsite.technology.grammar.initStatic;
/**
 * 一道大多数人都会做错的JVM题
 * @author lzhcode
 *
 */
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

	TestStatic2() {
		System.out.println("3");
		System.out.println("a=" + a + ",b=" + b);
	}

	public static void staticFunction() {
		System.out.println("4");
	}

	int a = 110;
	static int b = 112;
}
//确的答案是：
//2
//3
//a=110,b=0
//1
//4

//类的准备阶段需要做是为类变量分配内存并设置默认值，因此类变量st为null、b为0；
//（需要注意的是如果类变量是final，编译时javac将会为value生成ConstantValue属性，
//在准备阶段虚拟机就会根据ConstantValue的设置将变量设置为指定的值，如果这里这么定义：
//static final int b=112,那么在准备阶段b的值就是112，而不再是0了。）


//这里面牵涉到一个冷知识，就是在嵌套初始化时有一个特别的逻辑。特别是内嵌的这个变量恰好是个静态成员，
//而且是本类的实例。 这会导致一个有趣的现象：“实例初始化竟然出现在静态初始化之前”。 其实并没有提前，
//你要知道java记录初始化与否的时机 
 

//首先在执行此段代码时，首先由main方法的调用触发静态初始化。
//在初始化Test 类的静态部分时，遇到st这个成员。
//但凑巧这个变量引用的是本类的实例。
//那么问题来了，此时静态初始化过程还没完成就要初始化实例部分了。是这样么？
//从人的角度是的。但从java的角度，一旦开始初始化静态部分，无论是否完成，后续都不会再重新触发静态初始化流程了。
//因此在实例化st变量时，实际上是把实例初始化嵌入到了静态初始化流程中，并且在楼主的问题中，嵌入到了静态初始化的起始位置。这就导致了实例初始化完全至于静态初始化之前。这也是导致a有值b没值的原因。
//最后再考虑到文本顺序，结果就显而易见了。

//这里主要的点之一：实例初始化不一定要在类初始化结束之后才开始初始化 
//这题可以理解为先初始化实例再初始化静态逻辑
