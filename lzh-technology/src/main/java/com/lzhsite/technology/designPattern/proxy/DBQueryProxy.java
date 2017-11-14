package com.lzhsite.technology.designPattern.proxy;

public class DBQueryProxy implements IDBQuery {
	private DBQuery real = null;

	@Override
	public String request() {
		// ��������Ҫ��ʱ�� ���Ŵ�����ʵ���󣬴������̿��ܺ���
		if (real == null)
			real = new DBQuery();
		// �ڶ��̻߳����£����ﷵ��һ������࣬������Futureģʽ
		return real.request();
	}
}
