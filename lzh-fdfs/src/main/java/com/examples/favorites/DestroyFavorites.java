package com.examples.favorites;

import com.examples.oauth2.Log;
import com.weibo4j.Favorite;
import com.weibo4j.model.Favorites;
import com.weibo4j.model.WeiboException;

public class DestroyFavorites {

	public static void main(String[] args) {
		String access_token = args[0];
		String id = args[1];
		Favorite fm = new Favorite(access_token);
		try {
			Favorites favors = fm.destroyFavorites(id);
			Log.logInfo(favors.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
