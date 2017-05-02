package com.weibo4j;

import com.weibo4j.model.MessageModel;
import com.weibo4j.model.PostParameter;
import com.weibo4j.model.WeiboException;
import com.weibo4j.util.WeiboConfig;

public class Message extends Weibo {

 
	public Message(String access_token) {
		this.access_token = access_token;
	}
 
	public String getUid() throws WeiboException {
		return client.get(
				WeiboConfig.getValue("baseURL") + "account/get_uid.json",
				access_token).asJSONObject().toString();
	}
	
	public MessageModel sendPrivateMeaasge(String type,String data,String receiver_id,int save_sender_box) throws WeiboException {
		return new MessageModel(client.get(
				WeiboConfig.getValue("baseURL") + "messages/reply.json",
				new PostParameter[] { new PostParameter("uid", getUid()),
					                  new PostParameter("type", type),
					                  new PostParameter("data", data),
					                  new PostParameter("receiver_id",receiver_id),
					                  new PostParameter("save_sender_box",save_sender_box)},
				access_token));
	}
	 

}
