package com.weibo.examples.favorites;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Favorite;
import com.weibo.weibo4j.model.WeiboException;

public class DestroyFavoritesTagsBatch {

	public static void main(String[] args) {
		String access_token = args[0];
		Favorite fm = new Favorite(access_token);
		boolean result = false;
		String ids = args[1];
		try {
			result = fm.destroyFavoritesTagsBatch(ids);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {

			e.printStackTrace();
		}
	}

}