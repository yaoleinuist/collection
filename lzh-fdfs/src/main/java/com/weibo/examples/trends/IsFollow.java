package com.weibo.examples.trends;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Trend;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONObject;

public class IsFollow {

	public static void main(String[] args) {
		String access_token = args[0];
		String trend_name = args[1];
		Trend tm = new Trend(access_token);
		try {
			JSONObject result = tm.isFollow(trend_name);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
