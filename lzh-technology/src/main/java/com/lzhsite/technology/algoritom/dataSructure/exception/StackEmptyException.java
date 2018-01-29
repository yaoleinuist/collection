package com.lzhsite.technology.algoritom.dataSructure.exception;

//堆栈为空时出栈或取栈顶元素抛出此异常
public class StackEmptyException extends RuntimeException{
	
	public StackEmptyException(String err) {
		super(err);
	}	
}
