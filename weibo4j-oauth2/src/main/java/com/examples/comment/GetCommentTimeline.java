package com.examples.comment;

import com.examples.oauth2.Log;
import com.weibo4j.Comments;
import com.weibo4j.model.CommentWapper;
import com.weibo4j.model.WeiboException;

public class GetCommentTimeline {

	public static void main(String[] args) {
		String access_token = args[0];
		Comments cm = new Comments(access_token);
		try {
			CommentWapper comment = cm.getCommentTimeline();
			Log.logInfo(comment.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
