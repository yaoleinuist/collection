package com.weibo.examples.place;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Place;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONObject;

public class GetUserInfoInLbs {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Place p = new Place(access_token);
		try {
			JSONObject sw = p.userInfoInLBS(uid);
			Log.logInfo(sw.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
