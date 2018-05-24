package com.lzhsite.technology.algoritom.practise;

import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

//题目描述 
//给出一个长度为 n 的数列 { a[1] , a[2] , a[3] , ... , a[n] }，以及 m 组询问 ( l[i] , r[i] , k[i])。
//求数列下标区间在 [ l[i] , r[i] ] 中有多少数在该区间中的出现次数与 k[i] 互质（最大公约数为1）。
//输入描述:
//第一行，两个正整数 n , m (1 ≤ n, m ≤ 50000)。
//第二行，n 个正整数 a[i] (1 ≤ a[i] ≤ n)描述这个数列。
//接下来 m 行，每行三个正整数 l[i] , r[i] , k[i] (1 ≤ l[i] ≤ r[i] ≤ n, 1 ≤ k[i] ≤ n)，描述一次询问。
//输出描述:
//输出 m 行，即每次询问的答案。

//两个坑
//1.a[i]小标从1开始
//2.注意对这句话的理解"有多少数在该区间中的出现次数与 k[i] 互质"
public class 数列互质 {

	static Boolean isrp2(int x, int y) {
		if(x==y){
			return false;
		}
		return y == 0 ? (x == 0 ? false : true) : isrp2(y, x % y);
	}

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

	public   HashMap getNumMap(Integer nums[], int begin, int end) {
		HashMap map = new HashMap<Object, Integer>();
		for (int i = begin - 1; i <= end - 1; i++) {
			if (map.get(nums[i]) == null) {
				map.put(nums[i], 1);
			} else {
				Integer count = (Integer) map.get(nums[i]);
				map.put(nums[i], count + 1);
			}
		}
		return map;
	}

	public static void main(String[] args) {
		// 输入包括一个整数n(1 ≤ n ≤ 1,000,000,000)
		Scanner s = new Scanner(System.in);
		// 输入的第一行为一个正整数n(1 ≤ n ≤ 10^5)
		Integer n = s.nextInt();
		Integer m = s.nextInt();
		Integer a[] = new Integer[n];
		Integer b[][] = new Integer[m][3];

		for (int i = 0; i < a.length; i++) {
			a[i] = s.nextInt();
		}
		for (int i = 0; i < m; i++) {
			b[i][0] = s.nextInt();
			b[i][1] = s.nextInt();
			b[i][2] = s.nextInt();
		}
		// 如果你使用HashMap
		// 1.同时遍历key和value时，keySet与entrySet方法的性能差异取决于key的具体情况，如复杂度（复杂对象）、
		// 离散度、冲突率等。换言之，取决于HashMap查找value的开销。
		// entrySet一次性取出所有key和value的操作是有性能开销的，当这个损失小于HashMap查找value的开销时，
		// entrySet的性能优势就会体现出来。例如上述对比测试中，当key是最简单的数值字符串时，keySet可能反而会更高效，
		// 耗时比entrySet少10%。总体来说还是推荐使用entrySet。因为当key很简单时，其性能或许会略低于keySet，但却是可控的；
		// 而随着key的复杂化，entrySet的优势将会明显体现出来。当然，我们可以根据实际情况进行选择
		// 2.只遍历key时，keySet方法更为合适，因为entrySet将无用的value也给取出来了，浪费了性能和空间。在上述测试结果中，
		// keySet比entrySet方法耗时少23%。
		// 3.只遍历value时，使用vlaues方法是最佳选择，entrySet会略好于keySet方法

		// 如果你使用TreeMap
		// 1.同时遍历key和value时，与HashMap不同，entrySet的性能远远高于keySet。
		// 这是由TreeMap的查询效率决定的，也就是说，TreeMap查找value的开销较大，明显高于entrySet一次性
		// 取出所有key和value的开销。因此，遍历TreeMap时强烈推荐使用entrySet方法。
		// 2.只遍历key时，keySet方法更为合适，因为entrySet将无用的value也给取出来了，浪费了性能和空间。
		// 3.只遍历value时，使用vlaues方法是最佳选择，entrySet也明显优于keySet方法。
		AtomicReferenceArray<Integer> atomicReferenceArray = new AtomicReferenceArray<Integer>(m);
		CountDownLatch latch = new CountDownLatch(m);
		

		for (int i=0; i < m; i++) {
			
			Integer row = new Integer(i);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int count =  0;
					HashMap<Object, Integer> nummap = new 数列互质().getNumMap(a, b[row][0], b[row][1]);
					for (Integer value : nummap.values()) {
						if (isrp2(value, b[row][2])) {
							count++;
						}
					}
					atomicReferenceArray.set(row, count);
					latch.countDown();
				}
			}).start();

		}
		try {
			latch.await();
			for (int j = 0; j < atomicReferenceArray.length(); j++) {
				System.out.println(atomicReferenceArray.get(j));
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

	}

}
////////////////////////////////////////上面的写法运行超时考虑,网上查到了类似的题目思路//////////////////////////////////////////////
//统计[a,b]这个区间中和n互质的数的个数 （容斥+数组队列）
//很明显的容斥问题，那么假如1-12，n=30；
//n里面的约数2,3,5，那么进行排列组合，2,3,5,-2*3,-2*5,-3*5，2*3*5 ,,,怎么表示，有两种方法，一种是状态压缩，另一种是数组队列....
//我们重新给这三个数安排一下位置，，2, 3, 2*3, 5, 2*5, 3*5, 2*3*5  
//
//题意：就是让你求(a,b)区间于n互质的数的个数.
//
//分析：我们可以先转化下：用(1,b)区间与n互质的数的个数减去(1,a-1)区间与n互质的数的个数,那么现在就转化成求(1,m)区间于n互质的数的个数,如果要求的是(1,n)区间与n互质的数的个数的话，我们直接求出n的欧拉函数值即可，可是这里是行不通的!我们不妨换一种思路：就是求出(1,m)
//区间与n不互质的数的个数，假设为num，那么我们的答案就是：m-num!现在的关键就是：怎样用一种最快的方法求出(1,m)区间与n不互质的数的个数？方法实现：我们先求出n的质因子(因为任何一个数都可以分解成若干个质数相乘的),如何尽快地求出n的质因子呢？我们这里又涉及两个好的算法了!
//第一个：用于每次只能求出一个数的质因子,适用于题目中给的n的个数不是很多，但是n又特别大的;(http://www.cnblogs.com/jiangjing/archive/2013/06/03/3115399.html)
//第二个：一次求出1~n的所有数的质因子,适用于题目中给的n个数比较多的，但是n不是很大的。(http://www.cnblogs.com/jiangjing/archive/2013/06/01/3112035.html)
//本题适用第一个算法！举一组实例吧:假设m=12,n=30.
//
//第一步：求出n的质因子：2,3,5；
//
//第二步：(1,m)中是n的因子的倍数当然就不互质了(2,4,6,8,10)->n/2  6个,(3,6,9,12)->n/3  4个,(5,10)->n/5  2个。
//      如果是粗心的同学就把它们全部加起来就是：6+4+2=12个了，那你就大错特错了,里面明显出现了重复的,我们现在要处理的就是如何去掉那些重复的了！
//
//第三步：这里就需要用到容斥原理了，公式就是：n/2+n/3+n/5-n/(2*3)-n/(2*5)-n/(3*5)+n/(2*3*5).
//
//第四步:我们该如何实现呢？我在网上看到有几种实现方法：dfs(深搜)，队列数组，位运算三种方法都可以！上述公式有一个特点：n除以奇数个数相乘的时候是加，
//     n除以偶数个数相乘的时候是减。我这里就写下用队列数组如何实现吧：我们可以把第一个元素设为-1然后具体看代码如何实现吧！
