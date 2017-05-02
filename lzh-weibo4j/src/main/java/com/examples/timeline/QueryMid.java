package com.examples.timeline;

import com.examples.oauth2.Log;
import com.weibo4j.Timeline;
import com.weibo4j.model.WeiboException;
import com.weibo4j.org.json.JSONObject;

public class QueryMid {

	public static void main(String[] args) {
		String access_token = args[0];
		String id = args[1];
		Timeline tm = new Timeline(access_token);
		try {
			JSONObject mid = tm.queryMid(1, id);
			Log.logInfo(mid.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
