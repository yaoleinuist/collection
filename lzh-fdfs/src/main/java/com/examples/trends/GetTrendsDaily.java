package com.examples.trends;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Trend;
import com.weibo4j.model.Trends;
import com.weibo4j.model.WeiboException;

public class GetTrendsDaily {
	public static void main(String[] args) {
		String access_token = args[0];
		Trend tm = new Trend(access_token);
		try {
			List<Trends> trends = tm.getTrendsDaily();
			for(Trends ts : trends){
				Log.logInfo(ts.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}


}


