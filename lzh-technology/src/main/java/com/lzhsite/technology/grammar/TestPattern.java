package com.lzhsite.technology.grammar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestPattern {

	public static void main(String[] args) {

		System.out.println("pager.offset=10&pager.offset=20".replaceAll("(pager.offset=[0-9&]*)", ""));

		// Tgroup是针对（）来说的，group（0）就是指的整个串，group（1）
		// 指的是第一个括号里的东西，group（2）指的第二个括号里的东西
		String str = "Hello,World! in Java.";
		Pattern pattern = Pattern.compile("W(or)(ld!)");
		Matcher matcher = pattern.matcher(str);
		while (matcher.find()) {
			System.out.println("Group 0:" + matcher.group(0));// 得到第0组——整个匹配
			System.out.println("Group 1:" + matcher.group(1));// 得到第一组匹配——与(or)匹配的
			System.out.println("Group 2:" + matcher.group(2));// 得到第二组匹配——与(ld!)匹配的，组也就是子表达式
			System.out.println("Start 0:" + matcher.start(0) + " End 0:" + matcher.end(0));// 总匹配的索引
			System.out.println("Start 1:" + matcher.start(1) + " End 1:" + matcher.end(1));// 第一组匹配的索引
			System.out.println("Start 2:" + matcher.start(2) + " End 2:" + matcher.end(2));// 第二组匹配的索引
			System.out.println(str.substring(matcher.start(0), matcher.end(1)));// 从总匹配开始索引到第1组匹配的结束索引之间子串——Wor
		}
	}
}
