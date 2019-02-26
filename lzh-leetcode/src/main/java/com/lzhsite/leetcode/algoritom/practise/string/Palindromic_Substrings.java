package com.lzhsite.leetcode.algoritom.practise.string;

import java.util.LinkedList;
import java.util.List;

/**
 * 回文子字符串
 * Given a string, your task is to count how many palindromic substrings in this string.
 * The substrings with different start indexes or end indexes are counted as 
 * different substrings even they consist of same characters.
 * 
 * Example 1:
 * Input: "abc"
 * Output: 3
 * Explanation: Three palindromic strings: "a", "b", "c".
 *
 * Example 2:
 * Input: "aaa"
 * Output: 6
 * Explanation: Six palindromic strings: "a", "a", "a", "aa", "aa", "aaa".
 * @author lzhcode
 *
 */
public class Palindromic_Substrings {
//   思路：
//
//	1.在子串中使用由中间向两边扩展的方式判断一个字符串是否是回文字符串。 

	    public List<String> extendRoundCenter(String s, int start, int end) {
	        List<String> res = new LinkedList<>();
	        while (start >= 0 && end < s.length() && s.charAt(start) == s.charAt(end)) {
	            res.add(s.substring(start,end+1));
	            start --;
	            end ++;
	        }
	        return res;
	    }
//	重点: 每次向两边扩展的时候就说明已经是一个回文子串了，使用一个list列表来存储回文子串，最后返回给调用他的函数进行汇总。
//
//	2.在循环的时候，使用一个list表来存储每次获取的结果列表，并且在判断字符串某一部分是不是回文串的时候要注意分奇数和偶数，
//	奇数由自身开始扩展，偶数由自身和自身后两个字符开始扩展。

	    public List<String> countSubstrings(String s) {
	        List<String> res= new LinkedList<>();
	        if (s == null || s.length() == 0) {
	            return res;
	        }
	        int length = s.length();
	        for (int i = 0; i < length; i++) {
	            res.addAll(extendRoundCenter(s, i, i));
	            res.addAll(extendRoundCenter(s, i, i+1));
	        }
	        System.out.println(res);
	        System.out.println(res.size());
	        return res;
	    }
	    public static void main(String[] args) {
	    	Palindromic_Substrings lee647 = new Palindromic_Substrings();
	        lee647.countSubstrings("abcc");
	    }
}
