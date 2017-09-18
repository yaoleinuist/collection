package com.weibo.examples.place;

import java.util.List;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Place;
import com.weibo.weibo4j.model.PoisitionCategory;
import com.weibo.weibo4j.model.WeiboException;

public class GetPoisCategory {

	public static void main(String[] args) {
		String access_token = args[0];
		Place p = new Place(access_token);
		try {
			List<PoisitionCategory> list = p.poisCategory();
			for (PoisitionCategory pois : list) {
				Log.logInfo(pois.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
