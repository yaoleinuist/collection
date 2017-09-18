package com.weibo.examples.location;

import java.util.List;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Location;
import com.weibo.weibo4j.model.Poisition;
import com.weibo.weibo4j.model.WeiboException;

public class ShowPoisBatch {

	public static void main(String[] args) {
		String access_token = args[0];
		String srcids = args[1];
		Location l = new Location(access_token);
		try {
			List<Poisition> list = l.showPoisBatch(srcids);
			for (Poisition p : list) {
				Log.logInfo(p.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
