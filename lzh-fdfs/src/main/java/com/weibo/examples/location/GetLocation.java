package com.weibo.examples.location;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Location;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONObject;

public class GetLocation {

	public static void main(String[] args) {
		String access_token = args[0];
		String j = args[1];
		Location l = new Location(access_token);
		try {
			JSONObject json = l.getLocation(j);
			Log.logInfo(json.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
