package com.weibo.examples.timeline;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Timeline;
import com.weibo.weibo4j.model.StatusWapper;
import com.weibo.weibo4j.model.WeiboException;

public class GetRepostTimeline {

	public static void main(String[] args) {
		String access_token = args[0];
		String id = args[1];
		Timeline tm = new Timeline(access_token);
		try {
			StatusWapper status = tm.getRepostTimeline(id);
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
