package com.weibo.examples.place;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Place;
import com.weibo.weibo4j.model.Places;
import com.weibo.weibo4j.model.WeiboException;

public class GetPoisShow {

	public static void main(String[] args) {
		String access_token = args[0];
		String poiid = args[1];
		Place p = new Place(access_token);
		try {
			Places pl = p.poisShow(poiid);
			Log.logInfo(pl.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
