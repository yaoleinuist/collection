package com.weibo.examples.search;

import com.weibo.weibo4j.Search;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONArray;

public class SearchSuggestionsAtUsers {

	public static void main(String[] args) {
		String access_token = args[0];
		String q = args[1];
		int type = Integer.parseInt(args[2]);
		Search search = new Search(access_token);
		try {
			JSONArray jo = search.searchSuggestionsAtUsers(q, type);
			System.out.println(jo.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
