package com.lzhsite.leetcode.algoritom.practise.dynamicProgramming;
/**
 * 牛牛新买了一本算法书，算法书一共有n页，页码从1到n。
 * 牛牛于是想了一个算法题目：在这本算法书页码中1每个数字分别出现了多少次？ 
 * @author lzhcode
 *
 */
public class 一的数字 {
	
	Long Sum1s(Long n) {
		Long iCount = 0L;

		Long iFactor = 1L; //1,10,100...

		Long iLowerNum = 0L;
		Long iCurrNum = 0L;
		Long iHigherNum = 0L;

		while (n / iFactor != 0) {
			iLowerNum = n - (n / iFactor) * iFactor; //n%iFactor
			iCurrNum = (n / iFactor) % 10;
			iHigherNum = n / (iFactor * 10);

			switch (iCurrNum.intValue()) {
			case 0:
				iCount += iHigherNum * iFactor;
				break;
			case 1:
				iCount += iHigherNum * iFactor + iLowerNum + 1;
				break;
			default:
				iCount += (iHigherNum + 1) * iFactor;
				break;
			}

			iFactor *= 10;
		}

		return iCount;
	}
	
	
	public static void main(String[] args) {
		System.out.println("iLowerNum="+(23843-(23843/1)*1)+" "+(23843 / 1) % 10 +" iHigherNum="+23843/(10*1));
		System.out.println("iLowerNum="+(23843-(23843/10)*10) +" "+(23843 / 10) % 10 +" iHigherNum="+23843/(10*10));
		System.out.println("iLowerNum="+(23843-(23843/100)*100) +" "+(23843 / 100) % 10 +" iHigherNum="+23843/(10*100));
		System.out.println("iLowerNum="+(23843-(23843/1000)*1000 )+" "+(23843 / 1000) % 10 +" iHigherNum="+23843/(10*1000));
		System.out.println("iLowerNum="+(23843-(23843/10000)*10000) +" "+(23843 / 10000) % 10 +" iHigherNum="+23843/(10*10000));
	}
		 
}
