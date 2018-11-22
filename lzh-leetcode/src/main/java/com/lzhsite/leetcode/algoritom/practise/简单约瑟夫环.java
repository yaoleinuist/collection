package com.lzhsite.leetcode.algoritom.practise;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class 简单约瑟夫环 {
	static final int totalNum = 41; // 总人数
	static final int KillMan = 3; // 自杀者报数

	static void Josephus(int alive) // 约瑟夫环算法
	{
		int[] man = new int[totalNum]; //用来记录第几个死的人
		int count = 1;
		int i = 0, pos = -1; //pos代表最初编号

		while (count <= totalNum) {
			do {
				pos = (pos + 1) % totalNum; // pos不断累加满totalNum就重新归0
				if (man[pos] == 0) //等于0表示还活着
					i++;     
				if (i == KillMan) // 该人自杀
				{
					i = 0;
					break;
				}
			} while (true);
			//break出来后的pos表示要死的人的下标号，count表示第几个要死的人
			man[pos] = count;
			//约瑟夫环编号就是第几个自杀的人
			System.out.printf("初始编号为的%2d个自杀！约瑟夫环编号为%2d", pos + 1, man[pos]);
		    System.out.printf(" ->\n"); // 输出换行
			count++;
		}
		System.out.printf("\n");
		System.out.printf("这%d需要存活的人初始位置应排在以下序号:\n", alive);
		alive = totalNum - alive;
		for (i = 0; i < totalNum; i++) {
			//System.out.print(man[i]+" ");
			if (man[i] > alive) {
				System.out.printf("初始编号:%d,约瑟夫环编号:%d\n", i + 1, man[i]);
			}
		}
		System.out.printf("\n");
	}

	
	public static void Josephus2(int alive) {
        // 初始化人数
        List<Integer> start = new ArrayList<Integer>();
        for (int i = 1; i <= totalNum; i++) {
            start.add(i);
        }
        // 从第K个开始计数
        int k = 0;
        while (start.size() > alive) {
            k = k + KillMan;
            // 第m人的索引位置
            k = k % (start.size()) - 1;
            // 判断是否到队尾
            if (k < 0) {
                System.out.println("初始编号:"+start.get(start.size() - 1));
                start.remove(start.size() - 1);
                k = 0;
            } else {
                System.out.println("初始编号:"+start.get(k));
                start.remove(k);
            }
        }
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
