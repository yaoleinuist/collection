package com.examples.tags;

import java.util.List;

import com.examples.oauth2.Log;
import com.weibo4j.Tags;
import com.weibo4j.model.Tag;
import com.weibo4j.model.WeiboException;

public class GetTags {

	public static void main(String[] args) {
		String access_token = args[0];
		String uid = args[1];
		Tags tm  = new Tags(access_token);
		try {
			List<Tag> tags = tm.getTags(uid);
			for(Tag tag:tags ){				
				Log.logInfo(tag.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
