package com.weibo.examples.comment;

import java.util.List;

import com.weibo.examples.oauth2.Log;
import com.weibo.weibo4j.Comments;
import com.weibo.weibo4j.model.Comment;
import com.weibo.weibo4j.model.WeiboException;

public class DestroyCommentBatch {

	public static void main(String[] args) {
		String access_token = args[0];
		String cids = args[1];
		Comments cm = new Comments(access_token);
		try {
			List<Comment> list = cm.destoryCommentBatch(cids);
			for (Comment c : list) {
				Log.logInfo(c.toString());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
}
