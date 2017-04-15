package com.examples.user;

import com.examples.oauth2.Log;
import com.weibo4j.Users;
import com.weibo4j.model.User;
import com.weibo4j.model.WeiboException;

public class ShowUser {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Users um = new Users(access_token);
		try {
			User user = um.showUserById(uid);
			Log.logInfo(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
