package com.weibo.examples.timeline;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Timeline;
import com.weibo.weibo4j.model.Status;
import com.weibo.weibo4j.model.WeiboException;

public class ShowStatus {

	public static void main(String[] args) {
		String access_token = args[0];
		String id = args[1];
		Timeline tm = new Timeline(access_token);
		try {
			Status status = tm.showStatus(id);
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
