package com.weibo.examples.friendships;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Friendships;
import com.weibo.weibo4j.model.User;
import com.weibo.weibo4j.model.UserWapper;
import com.weibo.weibo4j.model.WeiboException;

public class GetFollowers {

	public static void main(String[] args) {
		String access_token = args[0];
		Friendships fm = new Friendships(access_token);
		String screen_name = args[1];
		try {
			UserWapper users = fm.getFollowersByName(screen_name);
			for(User u : users.getUsers()){
				Log.logInfo(u.toString());
			}
			System.out.println(users.getNextCursor());
			System.out.println(users.getPreviousCursor());
			System.out.println(users.getTotalNumber());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
