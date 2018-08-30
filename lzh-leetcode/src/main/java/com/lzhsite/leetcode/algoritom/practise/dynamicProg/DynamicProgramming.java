package com.lzhsite.leetcode.algoritom.practise.dynamicProg;

import java.util.ArrayList;
import java.util.List;
/*
 题目：

输入两个整数 n 和 m，从数列1，2，3.......n 中 随意取几个数,
使其和等于 m ,要求将其中所有的可能组合列出来.


这道题就是一道典型的动态规划问题了，思路和背包问题差不多，m就相当于背包能容纳的重量了，就是从右往左校验，通过m，以及m-n进行下一次

也就是当前是printList(m,n)那接下来就是进行printList(m,n-1)和printList(m-n,n-1)进行递归

而终止条件是n<=0，以及m<0(m<0说明在上一次递归调用是减的n（相对于当前应该为n+1）是减多了，为负)，m==0时候说明正好找到，打印
*/
public class DynamicProgramming {
	public static void main(String[] args)  
    {  
        int n = 20;  
        int m = 8;  
        List<Integer> list = new ArrayList<>();  
  
        int up = n > m ? m : n;  
  
        printList(m, up, list);  
    }  
  
    /** 
     *  
     * @param m 
     *            剩些的能减去的数 
     * @param n 
     *            遍历的树列中的最大，从1，2，3...n右往左校验 
     * @param list 
     */  
    public static void printList(int m, int n, List<Integer> list)  
    {  
        if (m == 0)  
        {  
            System.out.println(list);  
            return;  
        }  
  
        if (n <= 0 || m < 0)  
        {  
            return;  
        }  
  
        List list1 = new ArrayList<>(list);  
        printList(m, n - 1, list);  //n选中
  
        list1.add(n);  
        printList(m - n, n - 1, list1);  //n未选中
  
    }  
}
