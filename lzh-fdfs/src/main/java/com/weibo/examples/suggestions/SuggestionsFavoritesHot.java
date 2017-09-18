package com.weibo.examples.suggestions;

import com.weibo.weibo4j.Suggestion;
import com.weibo.weibo4j.model.WeiboException;
import com.weibo.weibo4j.org.json.JSONArray;

public class SuggestionsFavoritesHot {

	public static void main(String[] args) {
		String access_token = args[0];
		Suggestion suggestion = new Suggestion(access_token);
		try {
			JSONArray jo = suggestion.suggestionsFavoritesHot();
			System.out.println(jo.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
