package com.weibo.examples.publicservice;

import com.weibo.weibo4j.PublicService;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONArray;

public class CityList {

	public static void main(String[] args) {
		String access_token = args[0];
		String province = args[1];
		PublicService ps = new PublicService(access_token);
		JSONArray jo;
		try {
			jo = ps.cityList(province);
			System.out.println(jo.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		
	}

}