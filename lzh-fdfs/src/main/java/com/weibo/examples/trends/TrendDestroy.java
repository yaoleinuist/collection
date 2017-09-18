package com.weibo.examples.trends;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Trend;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONObject;

public class TrendDestroy {

	public static void main(String[] args){
		String access_token = args[0];
		Trend tm = new Trend(access_token);
		int trendId = Integer.parseInt(args[1]);
		try {
			JSONObject result = tm.trendsDestroy(trendId);
			Log.logInfo(String.valueOf(result));
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
