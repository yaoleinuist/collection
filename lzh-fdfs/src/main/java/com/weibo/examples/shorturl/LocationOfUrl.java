package com.weibo.examples.shorturl;

import com.weibo.weibo4j.ShortUrl;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONObject;

public class LocationOfUrl {

	public static void main(String[] args) {
		String access_token = args[0];
		String url = args[1];
		ShortUrl su = new ShortUrl(access_token);
		try {
			JSONObject jo = su.locationsOfUrl(url);
			System.out.println(jo.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
