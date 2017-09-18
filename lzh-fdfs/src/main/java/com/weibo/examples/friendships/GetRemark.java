package com.weibo.examples.friendships;

import com.weibo.weibo4j.Friendships;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONArray;

public class GetRemark {

	public static void main(String[] args) {
		String access_token = args[0];
		String uids = args[1];
		Friendships fm = new Friendships(access_token);
		try {
			JSONArray user = fm.getRemark(uids);
			System.out.println(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
