package com.weibo.examples.location;

import java.util.List;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Location;
import com.weibo.weibo4j.model.Geos;
import com.weibo.weibo4j.model.WeiboException;

public class GeoToAddress {

	public static void main(String[] args) {
		String access_token = args[0];
		String coordinate = args[1];
		Location l = new Location(access_token);
		try {
			List<Geos> list = l.geoToAddress(coordinate);
			for (Geos g : list) {
				Log.logInfo(g.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
