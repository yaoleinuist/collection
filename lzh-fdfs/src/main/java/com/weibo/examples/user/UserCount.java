package com.weibo.examples.user;

import java.util.List;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Users;
import com.weibo.weibo4j.model.UserCounts;
import com.weibo.weibo4j.model.WeiboException;

public class UserCount {

	public static void main(String[] args) {
		String access_token = args[0];
		String uids = args[1];
		Users um = new Users(access_token);
		try {
			List<UserCounts> user = um.getUserCount(uids);
			for (UserCounts u : user) {
				Log.logInfo(u.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
