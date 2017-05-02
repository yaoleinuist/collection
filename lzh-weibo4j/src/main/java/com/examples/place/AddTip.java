package com.examples.place;

import com.examples.oauth2.Log;
import com.weibo4j.Place;
import com.weibo4j.model.Status;
import com.weibo4j.model.WeiboException;

public class AddTip {
	
	public static void main(String[] args) {
		String access_token = args[0];
		String poiid = args[1];
		String status = args[2];
		Place p = new Place(access_token);
		try {
			Status s = p.addTip(poiid, status);
			Log.logInfo(s.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
