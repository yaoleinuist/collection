package com.examples.friendships;

import com.examples.oauth2.Log;
import com.weibo4j.Friendships;
import com.weibo4j.model.User;
import com.weibo4j.model.UserWapper;
import com.weibo4j.model.WeiboException;

public class GetFriendsChainFollowers {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Friendships fm = new Friendships(access_token);
		try {
			UserWapper  users = fm.getFriendsChainFollowers(uid);
			for(User s : users.getUsers()){
				Log.logInfo(s.toString());
			}
			System.out.println(users.getNextCursor());
			System.out.println(users.getPreviousCursor());
			System.out.println(users.getTotalNumber());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
