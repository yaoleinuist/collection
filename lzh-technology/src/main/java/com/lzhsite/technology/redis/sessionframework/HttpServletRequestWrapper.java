package com.lzhsite.technology.redis.sessionframework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
	private HttpSession session;

	private HttpServletRequest request;

	private HttpServletResponse response;

	private String sid = "";

	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	public HttpServletRequestWrapper(String sid, HttpServletRequest request) {
		super(request);
		this.sid = sid;
	}

	public HttpServletRequestWrapper(String sid, HttpServletRequest request, HttpServletResponse response) {
		super(request);
		this.request = request;
		this.response = response;
		this.sid = sid;
		if (this.session == null) {
			this.session = new HttpSessionWrapper(sid, super.getSession(false), request, response);
		}
	}

	@Override
	public HttpSession getSession(boolean create) {
		if (this.session == null) {
			if (create) {
				this.session = new HttpSessionWrapper(this.sid, super.getSession(create), this.request, this.response);
				return this.session;
			} else {
				return null;
			}
		}
		return this.session;
	}

	@Override
	public HttpSession getSession() {
		if (this.session == null) {
			this.session = new HttpSessionWrapper(this.sid, super.getSession(), this.request, this.response);
		}
		return this.session;
	}
}
