package com.examples.account;

import com.examples.oauth2.Log;
import com.weibo4j.Account;
import com.weibo4j.model.WeiboException;
import com.weibo4j.org.json.JSONObject;

public class GetUid {

	public static void main(String[] args) {
		String access_token = args[0];
		
 
		
		Account am = new Account(access_token);
		try {
			JSONObject uid = am.getUid();
			Log.logInfo(uid.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
