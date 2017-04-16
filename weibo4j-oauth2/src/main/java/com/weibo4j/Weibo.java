package com.weibo4j;

import com.weibo4j.http.HttpClient;

public class Weibo implements java.io.Serializable {

	private static final long serialVersionUID = 4282616848978535016L;

	protected static HttpClient client = new HttpClient();

//	如果希望自己设置HttpClient的各种参数，可以使用下面的构造方法
//	protected static HttpClient client = new HttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs,
//			int maxSize);
	
	protected String access_token;
	
	protected String uid;
	
	protected int ts; //精确到秒的十位时间戳
	
	protected String  sign;
	
	protected String  sign_type;
	
	protected String source; //为业务方分配的请求ID

}