package com.examples.location;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Location;
import com.weibo4j.model.Poisition;
import com.weibo4j.model.WeiboException;

public class SearchPoisByLocation {

	public static void main(String[] args) {
		String access_token = args[0];
		String q = args[1];
		Location l = new Location(access_token);
		try {
			List<Poisition> list = l.searchPoisByLocationByQ(q);
			for (Poisition p : list) {
				Log.logInfo(p.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
