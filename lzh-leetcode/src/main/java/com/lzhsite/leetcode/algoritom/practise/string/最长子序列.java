package com.lzhsite.leetcode.algoritom.practise.string;

import java.util.List;
/**
 * Input:
 * s = "abpcplea", d = ["ale","apple","monkey","plea"]
 *	Output:
 *	"apple"
 *	题目描述：删除 s 中的一些字符，使得它构成字符串列表 d 中的一个字符串，找出能构成的最长字符串。
 *	如果有多个相同长度的结果，返回字典序的最大字符串。
 */
public class 最长子序列 {
	
	public String findLongestWord(String s, List<String> d) {
	    String longestWord = "";
	    for (String target : d) {
	        int l1 = longestWord.length(), l2 = target.length();
	        //长度longestWord小，或则长度相同时字典序比longestWord小的target都不考虑
	        if (l1 > l2 || (l1 == l2 && longestWord.compareTo(target) < 0)) {
	            continue;
	        }
	        if (isValid(s, target)) {
	            longestWord = target;
	        }
	    }
	    return longestWord;
	}

	/**
	 * 
	 * @param s
	 * @param target
	 * @return
	 */
	private boolean isValid(String s, String target) {
	    int i = 0, j = 0;
	    //i指向s,j指向target，i走到最后时而，j没走到最后说明,不能通过删除s使它变成target
	    while (i < s.length() && j < target.length()) {
	        if (s.charAt(i) == target.charAt(j)) {
	            j++;
	        }
	        i++;
	    }
	    return j == target.length();
	}
}
