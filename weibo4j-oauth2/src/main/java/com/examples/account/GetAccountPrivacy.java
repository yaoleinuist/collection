package com.examples.account;

import com.examples.oauth2.Log;
import com.weibo4j.Account;
import com.weibo4j.model.Privacy;
import com.weibo4j.model.WeiboException;

public class GetAccountPrivacy {
 
	
	public static void main(String[] args) {
		String access_token = args[0];
		Account am = new Account(access_token);
		try {
			Privacy privacy = am.getAccountPrivacy();
			Log.logInfo(privacy.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}

