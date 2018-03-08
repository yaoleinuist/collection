package com.lzhsite.technology.algoritom.practise;

import java.util.Scanner;

public class 简单约瑟夫环 {
	static final int Num = 41; // 总人数
	static final int KillMan = 3; // 自杀者报数

	static void Josephus(int alive) // 约瑟夫环算法
	{
		int[] man = new int[Num];
		int count = 1;
		int i = 0, pos = -1; //pos代表最初编号

		while (count <= Num) {
			do {
				pos = (pos + 1) % Num; // 环处理
				if (man[pos] == 0) //等于0表示还活着
					i++;
				if (i == KillMan) // 该人自杀
				{
					i = 0;
					break;
				}
			} while (true);
			man[pos] = count;
			System.out.printf("第%2d个人自杀！约瑟夫环编号为%2d", pos + 1, man[pos]);
			if (count % 2 == 1) {
				System.out.printf(" -> ");
			} else {
				System.out.printf(" ->\n"); // 输出换行
			}
			count++;
		}
		System.out.printf("\n");
		System.out.printf("这%d需要存活的人初始位置应排在以下序号:\n", alive);
		alive = Num - alive;
		for (i = 0; i < Num; i++) {
			if (man[i] > alive) {
				System.out.printf("初始编号:%d,约瑟夫环编号:%d\n", i + 1, man[i]);
			}
		}
		System.out.printf("\n");
	}

	public static void main(String[] args) {
		int alive;
		Scanner input = new Scanner(System.in);
		System.out.printf("约瑟夫环问题求解!\n");
		System.out.printf("输入需要留存的人的数量:");
		alive = input.nextInt(); // 输入留存的人的数量
		Josephus(alive);

	}
}
