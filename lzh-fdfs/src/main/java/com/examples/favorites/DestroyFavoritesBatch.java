package com.examples.favorites;

import com.examples.oauth2.Log;
import com.weibo4j.Favorite;
import com.weibo4j.model.WeiboException;

public class DestroyFavoritesBatch {

	public static void main(String[] args) {
		String access_token = args[0];
		boolean result = false;
		String ids = args[1];
		Favorite fm = new Favorite(access_token);
		try {
			result = fm.destroyFavoritesTagsBatch(ids);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {

			e.printStackTrace();
		}
	}

}
