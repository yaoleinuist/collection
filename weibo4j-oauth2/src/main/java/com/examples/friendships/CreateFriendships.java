package com.examples.friendships;

import com.examples.oauth2.Log;
import com.weibo4j.Friendships;
import com.weibo4j.model.User;
import com.weibo4j.model.WeiboException;

public class CreateFriendships {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Friendships fm = new Friendships(access_token);
		try {
			User user = fm.createFriendshipsById(uid);
			Log.logInfo(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
