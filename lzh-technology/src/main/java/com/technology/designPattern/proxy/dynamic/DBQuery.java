package com.technology.designPattern.proxy.dynamic;

import com.technology.designPattern.proxy.IDBQuery;

public class DBQuery implements IDBQuery {
	public DBQuery() {
		//可能包含数据库连接等耗时操作
	}

	@Override
	public String request() {
		return "request string";
	}
}
