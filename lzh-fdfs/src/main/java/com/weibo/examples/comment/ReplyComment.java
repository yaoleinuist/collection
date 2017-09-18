package com.weibo.examples.comment;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Comments;
import com.weibo.weibo4j.model.Comment;
import com.weibo.weibo4j.model.WeiboException;

public class ReplyComment {

	public static void main(String[] args) {
		String access_token = args[0];
		String cid = args[1];
		String id = args[2];
		String comments = args[3];
		Comments cm = new Comments(access_token);
		try {
			Comment com = cm.replyComment(cid, id, comments);
			Log.logInfo(com.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
