package com.lzhsite.technology.nio.demo3;

import java.util.ArrayList;

import com.lzhsite.technology.nio.demo3.event.ServerListener;

public class Notifier {
	private static ArrayList listeners = null;
	private static Notifier instance = null;

	private Notifier() {
		listeners = new ArrayList();
	}

	/**
	 * 获取事件触发器
	 * 
	 * @return 返回事件触发器
	 */
	public static synchronized Notifier getNotifier() {
		if (instance == null) {
			instance = new Notifier();
			return instance;
		} else
			return instance;
	}

	/**
	 * 添加事件监听器
	 * 
	 * @param l
	 *            监听器
	 */
	public void addListener(ServerListener l) {
		synchronized (listeners) {
			if (!listeners.contains(l))
				listeners.add(l);
		}
	}

	public void fireOnAccept() throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onAccept();
	}

	public void fireOnAccepted(Request request) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			System.out.println("fireOnAccept");
			((ServerListener) listeners.get(i)).onAccepted(request);
			System.out.println((ServerListener) listeners.get(i));
		}
	}

	void fireOnRead(Request request) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onRead(request);

	}

	void fireOnWrite(Request request, Response response) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onWrite(request, response);

	}

	public void fireOnClosed(Request request) throws Exception {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onClosed(request);
	}

	public void fireOnError(String error) {
		for (int i = listeners.size() - 1; i >= 0; i--)
			((ServerListener) listeners.get(i)).onError(error);
	}
}
