package com.weibo.examples.favorites;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Favorite;
import com.weibo.weibo4j.model.Favorites;
import com.weibo.weibo4j.model.WeiboException;

public class ShowFavorite {

	public static void main(String[] args) {
		String access_token = args[0];
		Favorite fm = new Favorite(access_token);
		String id = args[1];
		try {
			Favorites favors = fm.showFavorites(id);
			Log.logInfo(favors.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}