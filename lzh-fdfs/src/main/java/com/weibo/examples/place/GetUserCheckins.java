package com.weibo.examples.place;

import java.util.List;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Place;
import com.weibo.weibo4j.model.Places;
import com.weibo.weibo4j.model.WeiboException;

public class GetUserCheckins {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Place p = new Place(access_token);
		try {
			List<Places> list = p.checkinsList(uid);
			for (Places pl : list) {
				Log.logInfo(pl.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
