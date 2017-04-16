package com.weibo4j;

public class Address extends Weibo {

	public Address(String access_token) {
		this.access_token = access_token;
		this.ts=Integer.parseInt(String.valueOf(System.currentTimeMillis()).toString().substring(0,10));
		this.sign_type="md5";
	}
}
