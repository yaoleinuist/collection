package com.weibo4j;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.weibo4j.model.DisplayWindowModel;
import com.weibo4j.model.WeiboException;
import com.weibo4j.util.HttpUtil;
import com.weibo4j.util.SignUtil;
import com.weibo4j.util.WeiboConfig;

public class DisplayWindow  extends Weibo {

 
	public DisplayWindow(String source) {
		this.ts=Integer.parseInt(String.valueOf(System.currentTimeMillis()).toString().substring(0,10));
		this.sign_type="md5";
		this.source=source;
		
		TreeMap<String, String> treeMap = new TreeMap<String, String>(new Comparator<String>() {

			public int compare(String o1, String o2) {
				// 指定排序器按照升序排列
				return o1.compareTo(o2);
			}
		});
		treeMap.put("source",source);
		treeMap.put("ts", ts+"");

		this.sign=SignUtil.getSign(treeMap, WeiboConfig
				.getValue("client_ID"));
		
	}
	
	public DisplayWindowModel showReceiveAddress() throws WeiboException {

		
		
		Map<String,String> params =new HashMap<>();
		params.put("ts",String.valueOf(System.currentTimeMillis()).toString().substring(0,10));
		params.put(sign_type,sign_type);
		params.put("source",source);
		params.put("uid",uid);
		params.put("access_token",access_token);
		
		return new DisplayWindowModel(HttpUtil.doGet("http://api.shop.sc.weibo.com/huajuan/address",params));
	}
}
