package com.weibo.examples.favorites;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Favorite;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONObject;

public class UpdateFavoritesTagsBatch {

	public static void main(String[] args) {
		String access_token = args[0];
		Favorite fm = new Favorite(access_token);
		String tid = args[1];
		String tag= args[2];
		try {
			JSONObject json = fm.updateFavoritesTagsBatch(tid, tag);
			Log.logInfo(json.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
