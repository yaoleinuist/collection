package com.examples.favorites;

import com.examples.oauth2.Log;
import com.weibo4j.Favorite;
import com.weibo4j.model.WeiboException;
import com.weibo4j.org.json.JSONObject;

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
