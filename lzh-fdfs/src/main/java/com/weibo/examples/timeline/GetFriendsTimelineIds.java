package com.weibo.examples.timeline;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Timeline;
import com.weibo.weibo4j.model.FriendsTimelineIds;
import com.weibo.weibo4j.model.WeiboException;

public class GetFriendsTimelineIds {

	public static void main(String[] args) {
		String access_token = args[0];
		Timeline tm = new Timeline(access_token);
		try {
			FriendsTimelineIds status = tm.getFriendsTimelineIds();
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
