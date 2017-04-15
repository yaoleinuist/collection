package com.examples.search;

import com.weibo4j.Search;
import com.weibo4j.model.WeiboException;
import com.weibo4j.org.json.JSONArray;

public class SearchSuggestionsUsers {

	public static void main(String[] args) {
		String access_token = args[0];
		String q = args[1];
		Search search = new Search(access_token);
		try {
			JSONArray jo = search.searchSuggestionsUsers(q);
			System.out.println(jo.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
