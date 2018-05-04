package com.lzhsite.technology.designPattern.strategy;
//策略模式属于对象的行为模式。其用意是针对一组算法，将每一个算法封装到具有共同接口的独立的类中，从而使得它们可以相互替换。
//策略模式使得算法可以在不影响到客户端的情况下发生变化。

//策略模式的使用场景：
//1）针对同一种问题的多种处理方式、仅仅是因为具体行为有差别时，
//2）需要安全的封装多种同一类型的操作时
//3）出现同一抽象类有多个子类，而又需要使用if-else或者switch-case来选择具体子类时。


//这样即使需要添加出租车的价格计算，只需要简单的新建一个类，让其继承自CalculateStrategy接口并实现其中的方法即可
//优点
//1）结构清晰明了、使用简单直观
//2）耦合度相对较低，扩展方便
//3）操作封装因为更为测地、数据更为安全
//缺点
//子类增多
public class TranficCalculator {
	
	CalculateStrategy mStrategy;

	public static void main(String[] args) {
		TranficCalculator calculator = new TranficCalculator();
		// 设置计算策略
		calculator.setStrategy(new BusStrategy());
		// 计算价格
		System.out.println("公交车乘10公里的价格：" + calculator.calculatePrice(10));

	}

	public void setStrategy(CalculateStrategy mStrategy) {
		this.mStrategy = mStrategy;
	}

	public int calculatePrice(int km) {
		return mStrategy.calculatePrice(km);
	}
}
