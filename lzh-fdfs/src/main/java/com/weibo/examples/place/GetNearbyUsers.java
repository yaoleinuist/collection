package com.weibo.examples.place;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Place;
import com.weibo.weibo4j.model.UserWapper;
import com.weibo.weibo4j.model.WeiboException;

public class GetNearbyUsers {

	public static void main(String[] args) {
		String access_token = args[0];
		String lat = args[1];
		String lon = args[2];
		Place p = new Place(access_token);
		try {
			UserWapper uw = p.nearbyUsers(lat, lon);
			Log.logInfo(uw.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
