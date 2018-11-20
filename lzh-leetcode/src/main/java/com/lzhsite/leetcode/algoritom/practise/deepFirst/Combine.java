package com.lzhsite.leetcode.algoritom.practise.deepFirst;

import java.util.Scanner;

public class Combine {
	//从n个数里面取出m个数组合排列
	//从后往前选取，选定位置i后，再在前i-1个里面选取m-1个。  
	//如 1 2 3 4 5 中选取 3 个  
	//1、选取5后，再在前4个里面选取2个，而前4个里面选取2个又是一个子问题，递归即可。  
	//2、如果不包含5，直接选定4，那么再在前3个里面选取2个，而前三个里面选取2个又是一个子问题，递归即可。  
	//3、如果也不包含4，直接选取3，那么再在前2个里面选取2个，刚好只有两个。  
	//纵向看，1、2、3刚好是一个for循环，初值为5，终值为m  
	//横向看，该问题为一个前i-1个中选m-1的递归。  

	private static int count=0;
	/**
	 * 
	 * @param arr     备选数组
	 * @param n       n个备选数
	 * @param m       选m个
	 * @param out     选中的数组
	 * @param outLen  需要选的总个数
	 */
	static void  comb(int arr[], int n, int m, int out[], int outLen)  
	{  
	    if(m == 0)  
	    {  
	        for (int j = 0; j < outLen; j++)  
	        	System.out.print(out[j]+" ");
	        System.out.println();
	        count++;
	        return;
	    }  
	  
	    for (int i = n; i > m; --i)  //从后往前依次选定一个  
	    {  
	        out[m-1] = arr[i-1]; //选定一个后  
	        comb(arr,i-1,m-1,out,outLen); // 从前i-1个里面选取m-1个进行递归  
	    }  
	}  
	
	public static void main(String[] args) {
	  int arr[]={0,1,2,3,4,5,6,7,8,9};
	  int out[]=new int[3];
	  comb(arr,10,3,out,3);
	  System.out.println(count);
	}
}
