package com.examples.friendships;

import com.examples.oauth2.Log;
import com.weibo4j.Friendships;
import com.weibo4j.model.WeiboException;

public class GetFriendsIds {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Friendships fm = new Friendships(access_token);
		try {
			String[] ids = fm.getFriendsIdsByUid(uid);
			for(String s : ids){
				Log.logInfo(s);
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
