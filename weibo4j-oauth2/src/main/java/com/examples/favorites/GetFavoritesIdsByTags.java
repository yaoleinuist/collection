package com.examples.favorites;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Favorite;
import com.weibo4j.model.FavoritesIds;
import com.weibo4j.model.WeiboException;

public class GetFavoritesIdsByTags {

	public static void main(String[] args) {
		String access_token = args[0];
		Favorite fm = new Favorite(access_token);
		String tid = args[1];
		try {
			List<FavoritesIds> favors = fm.getFavoritesIdsByTags(tid);
			for(FavoritesIds s : favors){
				Log.logInfo(s.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
