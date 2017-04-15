package com.examples.comment;

import com.examples.oauth2.Log;
import com.weibo4j.Comments;
import com.weibo4j.model.CommentWapper;
import com.weibo4j.model.WeiboException;

public class GetCommentById {

	public static void main(String[] args) {
		String access_token = args[0];
		String id = args[1];
		Comments cm = new Comments(access_token);
		try {
			CommentWapper comment = cm.getCommentById(id);
			Log.logInfo(comment.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
