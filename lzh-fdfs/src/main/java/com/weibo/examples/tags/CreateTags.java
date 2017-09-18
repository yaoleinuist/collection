package com.weibo.examples.tags;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Tags;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONArray;

public class CreateTags {

	public static void main(String[] args){
		String access_token = args[0];
		String tag = args[1];
		Tags tm = new Tags(access_token);
		try {
			JSONArray tags = tm.createTags(tag);
			Log.logInfo(tags.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
