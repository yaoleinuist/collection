package com.examples.publicservice;

import com.weibo4j.PublicService;
import com.weibo4j.model.WeiboException;
import com.weibo4j.org.json.JSONObject;

public class GetTimeZone {

	public static void main(String[] args) {
		String access_token = args[0];
		PublicService ps = new PublicService(access_token);
		try {
			JSONObject	jo = ps.getTomeZone();
			System.out.println(jo.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		
	}

}
