package com.examples.tags;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Tags;
import com.weibo4j.model.Tag;
import com.weibo4j.model.WeiboException;

public class GetTagsSuggestions {

	public static void main(String[] args) {
		String access_token = args[0];
		Tags tm = new Tags(access_token);
		List<Tag> tags = null;
		try {
			tags = tm.getTagsSuggestions();
			for(Tag tag : tags){				
				Log.logInfo(tag.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
