package com.lzhsite.technology.algoritom.practise;

public class MathUtil {
	
	static Boolean isrp(int a, int b) {
		if (a == 1 || b == 1) // 两个正整数中，只有其中一个数值为1，两个正整数为互质数
			return true;
		while (true) { // 求出两个正整数的最大公约数
			int t = a % b;
			if (t == 0)
				break;
			else {
				a = b;
				b = t;
			}
		}
		if (b > 1)
			return false;// 如果最大公约数大于1，表示两个正整数不互质
		else
			return true; // 如果最大公约数等于1,表示两个正整数互质
	}
	
 
}
