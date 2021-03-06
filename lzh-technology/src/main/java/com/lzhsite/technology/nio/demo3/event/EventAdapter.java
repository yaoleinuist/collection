package com.lzhsite.technology.nio.demo3.event;

import com.lzhsite.technology.nio.demo3.Request;
import com.lzhsite.technology.nio.demo3.Response;

public abstract class EventAdapter implements ServerListener {
	public EventAdapter() {
	}

	public void onError(String error) {
	}

	public void onAccept() throws Exception {
	}

	public void onAccepted(Request request) throws Exception {
	}

	public void onRead(Request request) throws Exception {
	}

	public void onWrite(Request request, Response response) throws Exception {
	}

	public void onClosed(Request request) throws Exception {
	}
}
