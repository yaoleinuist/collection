package com.weibo.examples.place;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Place;
import com.weibo.weibo4j.model.StatusWapper;
import com.weibo.weibo4j.model.WeiboException;

public class GetNearbyTimeLine {

	public static void main(String[] args) {
		String access_token = args[0];
		String lat = args[1];
		String lon = args[2];
		Place p = new Place(access_token);
		try {
			StatusWapper sw = p.nearbyTimeLine(lat, lon);
			Log.logInfo(sw.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
