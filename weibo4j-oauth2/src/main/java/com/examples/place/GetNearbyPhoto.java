package com.examples.place;

import com.examples.oauth2.Log;
import com.weibo4j.Place;
import com.weibo4j.model.StatusWapper;
import com.weibo4j.model.WeiboException;

public class GetNearbyPhoto {

	public static void main(String[] args) {
		String access_token = args[0];
		String lat = args[1];
		String lon = args[2];
		Place p = new Place(access_token);
		try {
			StatusWapper sw = p.nearbyPhoto(lat, lon);
			Log.logInfo(sw.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
