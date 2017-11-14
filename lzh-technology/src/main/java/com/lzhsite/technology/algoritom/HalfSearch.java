package com.lzhsite.technology.algoritom;
 

/*
假设对n个元素的折半查找需要消耗的时间为t(n)。容易知道：
如果n = 1，则t(n) = c1
如果n > 1，则t(n) = t(n/2) + c2
其中n/2需要取整，c1、c2都是常数

对于正整数n，可以有：
t(n) = t(n/2) + c2
= t(n/4) + 2*c2
= t(n/8) + 4*c2
= ...
= t(n/(2的k次方)) + k*c2
一直推演下去，直到n/(2的k次方)等于1，也就是k = log2(n)，此时等式变为：
t(n) = t(1) + k*c2
= c1 + log2(n)*c2

于是时间复杂度为log2(n)。注意log2(n)和log(n)其实是同样的复杂度，因为它们之间仅仅差了一个常量系数而已。

这个是不严格的推导，因为没有考虑整数除以2之后可能有余数的情况。但即使有余数，也是不影响时间复杂度的。
*/

public class HalfSearch {
	public static int halfSearch(int a[], int x) {
		int mid, left, right;
		left = 0;
		right = a.length - 1;
		mid = (left + right) / 2;
		while (a[mid] != x) {
			if (x > a[mid]) {
				left = mid + 1;
			} else if (x < a[mid]) {
				right = mid - 1;
			}
			mid = (left + right) / 2;
		}
		return mid;
	}

	public static void main(String[] args) {
		int a[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i] + "  ");
		}
		System.out.println();
		int s = 10;
		int index = halfSearch(a, s);
		System.out.println(s + "在数组中的下标是  " + index);

	}
}