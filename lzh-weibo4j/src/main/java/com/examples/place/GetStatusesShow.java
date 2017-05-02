package com.examples.place;

import com.examples.oauth2.Log;
import com.weibo4j.Place;
import com.weibo4j.model.Status;
import com.weibo4j.model.WeiboException;

public class GetStatusesShow {
	public static void main(String[] args) {
		String access_token = args[0];
		String id = args[1];
		Place p = new Place(access_token);
		try {
			Status s = p.statusesShow(id);
			Log.logInfo(s.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
