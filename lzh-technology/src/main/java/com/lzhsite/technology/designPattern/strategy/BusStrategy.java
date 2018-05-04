package com.lzhsite.technology.designPattern.strategy;

public class BusStrategy implements CalculateStrategy {

	@Override
	public int calculatePrice(int km) {
		// TODO Auto-generated method stub
		 int extraTotal = km - 10;
	        int extraFactor = extraTotal / 5;
	        int fraction = extraTotal % 5;
	        int price = 1 + extraFactor * 1;
	        return fraction > 0 ? ++price : price;
	}

}
