package com.weibo.examples.friendships;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Friendships;
import com.weibo.weibo4j.model.User;
import com.weibo.weibo4j.model.UserWapper;
import com.weibo.weibo4j.model.WeiboException;

public class GetFollowsActive {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Friendships fm = new Friendships(access_token);
		try {
			UserWapper users = fm.getFollowersActive(uid);
			for(User u : users.getUsers()){
				Log.logInfo(u.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
