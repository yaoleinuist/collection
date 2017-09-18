package com.weibo.weibo4j.model;

import com.weibo.weibo4j.http.Response;
import com.weibo.weibo4j.org.json.JSONException;
import com.weibo.weibo4j.org.json.JSONObject;

public class MessageModel extends WeiboResponse {

	private String result;
	private int sender_id;
	private int receiver_id;
	private String type;
	private String data;

	
	public MessageModel(Response res ) throws WeiboException {
		super(res);
		init(res.asJSONObject());
	}
	
	public MessageModel(JSONObject json) throws WeiboException {
		super();
		init(json);
	}
	
	private void init(JSONObject json)  throws WeiboException {
		// TODO Auto-generated method stub
		if(json!=null){
			try {
				result = json.getString("result");
				sender_id = json.getInt("sender_id");
				receiver_id = json.getInt("receiver_id");
				type = json.getString("type");
				data = json.getString("data");
	 
			} catch (JSONException jsone) {
				throw new WeiboException(jsone.getMessage() + ":" + json.toString(), jsone);
			}
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getSender_id() {
		return sender_id;
	}

	public void setSender_id(int sender_id) {
		this.sender_id = sender_id;
	}

	public int getReceiver_id() {
		return receiver_id;
	}

	public void setReceiver_id(int receiver_id) {
		this.receiver_id = receiver_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
