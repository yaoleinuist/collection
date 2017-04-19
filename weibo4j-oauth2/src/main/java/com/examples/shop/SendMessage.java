package com.examples.shop;

import com.weibo4j.Message;
import com.weibo4j.model.WeiboException;

public class SendMessage {

	/*
	 * accessToken=2.008T4N4D09TpN9b6cf4a61d00UAKKJ 
	 * uid=3091442725
	 * code=223f4d35963be49918159037d5bd08a7
	 */
	public static void main(String[] args) {

		String access_token = "2.008T4N4D09TpN9b6cf4a61d00UAKKJ ";

		String type = "text";
		String data = "%22text%22%3a+%22%e7%ba%af%e6%96%87%e6%9c%ac%e5%9b%9e%e5%a4%8d%22";
		String receiver_id = "3091442725";
		int save_sender_box = 1;

		Message message = new Message(access_token);
		try {
			message.sendPrivateMeaasge(type, data, receiver_id, save_sender_box);
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
