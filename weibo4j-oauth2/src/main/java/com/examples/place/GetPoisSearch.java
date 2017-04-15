package com.examples.place;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Place;
import com.weibo4j.model.Places;
import com.weibo4j.model.WeiboException;

public class GetPoisSearch {

	public static void main(String[] args) {
		String access_token = args[0];
		String keyword = args[1];
		Place p = new Place(access_token);
		try {
			List<Places> list = p.poisSearch(keyword);
			for (Places pl : list) {
				Log.logInfo(pl.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
