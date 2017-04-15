package com.examples.tags;

import com.examples.oauth2.Log;
import com.weibo4j.Tags;
import com.weibo4j.model.WeiboException;
import com.weibo4j.org.json.JSONObject;

public class DestroyTag {

	public static void main(String[] args) {
		String access_token = args[0];
		int tag_id = Integer.parseInt(args[1]);
		Tags tm = new Tags(access_token);
		try {
			JSONObject result = tm.destoryTag(tag_id);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
