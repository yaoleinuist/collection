package com.examples.friendships;

import com.examples.oauth2.Log;
import com.weibo4j.Friendships;
import com.weibo4j.model.WeiboException;
import com.weibo4j.org.json.JSONObject;

public class GetFriendshipsByName {
	public static void main(String[] args) {
		String access_token = args[0];
		String source = args[1];
		String target = args[2];
		Friendships fm = new Friendships(access_token);
		try {
			JSONObject json = fm.getFriendshipsByName(source, target);
			Log.logInfo(json.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
