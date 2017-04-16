package com.examples.shop;


import com.weibo4j.Address;
import com.weibo4j.model.WeiboException;

public class ShowReceiveAddress {

	public static void main(String[] args) {
		  String access_token = args[0];
		  String uid = args[1];
		  String source="";
		  Address address=new Address(access_token, uid, source);
		  try {
			address.showReceiveAddress();
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
