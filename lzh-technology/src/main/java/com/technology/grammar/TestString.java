package com.technology.grammar;

public class TestString {

	
	public static void main(String[] args) {
		
/*		regionMatches（boolean ignoreCase，int toffset，String other，int ooffset，int len）；
		regionMatches（int toffset，String other，int ooffset，int len）； 
		上述两个方法用来比较两个字符串中指定区域的子串。入口参数中，
		用toffset和ooffset分别指出当前字符串中的子串起始位置和要与之比较的字符串中的子串起始地址；
		len 指出比较长度。前一种方法可区分大写字母和小写字母，如果在 boolean ignoreCase处写 true，表示将不区分大小写，
		写false则表示将区分大小写。而后一个方法认为大小写字母有区别。由此可见，实际上前一个方法隐含了后一个方法的功能
		
	*/
	
		String s1= "tsinghua";
		String s2="it is TsingHua"; 
		s1.regionMatches(0,s2,6,7); 
	}
}