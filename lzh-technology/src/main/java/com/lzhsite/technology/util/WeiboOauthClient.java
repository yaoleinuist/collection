package com.lzhsite.technology.util;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.lzhsite.core.utils.config.Property;
 
 

public class WeiboOauthClient {
	
	private String WEIBO_APPKEY=Property.getProperty("client_ID");

	private String WEIBO_APPSECRET=Property.getProperty("client_SERCRET");

	private String WEIBO_CALLBACK=Property.getProperty("redirect_URI");

	private String WEIBO_APPKEY_OLD;

	private String WEIBO_APPSECRET_OLD;

	private String WEIBO_CALLBACK_OLD;


	private static WeiboOauthClient weiboAouth;
	
	private WeiboOauthClient(){
		
	}
	
	public static WeiboOauthClient getInstance(){
		
		if(weiboAouth==null){
			weiboAouth=new WeiboOauthClient();
		}
		
		return weiboAouth;
		
	}
	
	public String getCode() {
		return "redirect:https://api.weibo.com/oauth2/authorize?response_type=code&client_id=" + WEIBO_APPKEY
				+ "&redirect_uri=" + WEIBO_CALLBACK;
	}

	public String getOldCode() {
		return "redirect:https://api.weibo.com/oauth2/authorize?response_type=code&client_id=" + WEIBO_APPKEY_OLD
				+ "&redirect_uri=" + WEIBO_CALLBACK_OLD;
	}

	public String getAccessToken(String code) {
		String url = "https://api.weibo.com/oauth2/access_token";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", WEIBO_APPKEY));
		params.add(new BasicNameValuePair("client_secret", WEIBO_APPSECRET));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("redirect_uri", WEIBO_CALLBACK));
		try {
			HttpParams httpParameters = new BasicHttpParams();
 
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {// 如果状态码为200,就是正常返回
				JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
				return (String) jsonObject.get("access_token");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getOldAccessToken(String code) {
		String url = "https://api.weibo.com/oauth2/access_token";
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id", WEIBO_APPKEY_OLD));
		params.add(new BasicNameValuePair("client_secret", WEIBO_APPSECRET_OLD));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("redirect_uri", WEIBO_CALLBACK_OLD));
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);

			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(post);
			if (response.getStatusLine().getStatusCode() == 200) {// 如果状态码为200,就是正常返回
				JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
				return (String) jsonObject.get("access_token");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getOpenId(String accessToken) {
		String url = "https://api.weibo.com/2/account/get_uid.json?access_token=" + accessToken;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {// 如果状态码为200,就是正常返回
				String result = EntityUtils.toString(response.getEntity());
				JSONObject jsonObject = JSONObject.parseObject(result);
				return String.valueOf(jsonObject.get("uid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getOldOpenId(String accessToken) {
		String url = "https://api.weibo.com/2/account/get_uid.json?access_token=" + accessToken;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {// 如果状态码为200,就是正常返回
				String result = EntityUtils.toString(response.getEntity());
				JSONObject jsonObject = JSONObject.parseObject(result);
				return String.valueOf(jsonObject.get("uid"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public JSONObject getUser(String accessToken, String uid) {
		String url = "https://api.weibo.com/2/users/show.json?uid=" + uid + "&access_token=" + accessToken;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {// 如果状态码为200,就是正常返回
				String result = EntityUtils.toString(response.getEntity());
				JSONObject jsonObject = JSONObject.parseObject(result);
				return jsonObject;
			}
		} catch (Exception e) {

		}
		return null;
	}

	public JSONObject getOldUser(String accessToken, String uid) {
		String url = "https://api.weibo.com/2/users/show.json?uid=" + uid + "&access_token=" + accessToken;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {// 如果状态码为200,就是正常返回
				String result = EntityUtils.toString(response.getEntity());
				JSONObject jsonObject = JSONObject.parseObject(result);
				return jsonObject;
			}
		} catch (Exception e) {

		}
		return null;
	}

	public String getWEIBO_APPKEY() {
		return WEIBO_APPKEY;
	}

	public void setWEIBO_APPKEY(String wEIBO_APPKEY) {
		WEIBO_APPKEY = wEIBO_APPKEY;
	}

	public String getWEIBO_APPSECRET() {
		return WEIBO_APPSECRET;
	}

	public void setWEIBO_APPSECRET(String wEIBO_APPSECRET) {
		WEIBO_APPSECRET = wEIBO_APPSECRET;
	}

	public String getWEIBO_CALLBACK() {
		return WEIBO_CALLBACK;
	}

	public void setWEIBO_CALLBACK(String wEIBO_CALLBACK) {
		WEIBO_CALLBACK = wEIBO_CALLBACK;
	}

	public String getWEIBO_APPKEY_OLD() {
		return WEIBO_APPKEY_OLD;
	}

	public void setWEIBO_APPKEY_OLD(String wEIBO_APPKEY_OLD) {
		WEIBO_APPKEY_OLD = wEIBO_APPKEY_OLD;
	}

	public String getWEIBO_APPSECRET_OLD() {
		return WEIBO_APPSECRET_OLD;
	}

	public void setWEIBO_APPSECRET_OLD(String wEIBO_APPSECRET_OLD) {
		WEIBO_APPSECRET_OLD = wEIBO_APPSECRET_OLD;
	}

	public String getWEIBO_CALLBACK_OLD() {
		return WEIBO_CALLBACK_OLD;
	}

	public void setWEIBO_CALLBACK_OLD(String wEIBO_CALLBACK_OLD) {
		WEIBO_CALLBACK_OLD = wEIBO_CALLBACK_OLD;
	}

}
