package com.weibo.examples.timeline;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Timeline;
import com.weibo.weibo4j.model.Status;
import com.weibo.weibo4j.model.WeiboException;

public class UpdateStatus {

	public static void main(String[] args) {
		String access_token = args[0];
		String statuses = args[1];
		Timeline tm = new Timeline(access_token);
		try {
			Status status = tm.updateStatus(statuses);
			Log.logInfo(status.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}	
	}

}
