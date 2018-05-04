package com.lzhsite.technology.designPattern.strategy;

public class SubwayStrategy implements CalculateStrategy {

    public int calculatePrice(int km) {
        if (km <= 6) {
            return 3;
        } else if (km > 6 && km < 12) {
            return 4;
        } else if (km < 22 && km > 12) {
            return 5;
        } else if (km < 32 && km > 22) {
            return 6;
        }
        return 7;
    }
}