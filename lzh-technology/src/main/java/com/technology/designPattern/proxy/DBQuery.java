package com.technology.designPattern.proxy;

public class DBQuery implements IDBQuery {
	public DBQuery() {
		// 可能包含数据库连接等耗时操作
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String request() {
		return "request string";
	}
}
