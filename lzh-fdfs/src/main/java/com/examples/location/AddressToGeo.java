package com.examples.location;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Location;
import com.weibo4j.model.Geos;
import com.weibo4j.model.WeiboException;

public class AddressToGeo {

	public static void main(String[] args) {
		String access_token = args[0];
		String address = args[1];
		Location l = new Location(access_token);
		try {
			List<Geos> list = l.addressToGeo(address);
			for (Geos g : list) {
				Log.logInfo(g.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
