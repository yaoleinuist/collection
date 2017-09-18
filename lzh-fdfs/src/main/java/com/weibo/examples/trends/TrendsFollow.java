package com.weibo.examples.trends;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Trend;
import com.weibo.weibo4j.model.UserTrend;
import com.weibo.weibo4j.model.WeiboException;

public class TrendsFollow {

	public static void main(String[] args) {
		String access_token = args[0];
		Trend tm = new Trend(access_token);
		String trend_name = args[1];
		try {
			UserTrend ut = tm.trendsFollow(trend_name);
			Log.logInfo(ut.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
