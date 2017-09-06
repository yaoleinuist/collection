package com.examples.timeline;

import com.examples.oauth2.Log;
import com.weibo4j.Timeline;
import com.weibo4j.model.StatusWapper;
import com.weibo4j.model.WeiboException;

public class GetFriendsTimeline {

	public static void main(String[] args) {
		String access_token = args[0];
		Timeline tm = new Timeline(access_token);
		try {
			StatusWapper status = tm.getFriendsTimeline();
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
