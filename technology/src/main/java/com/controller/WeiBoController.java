package com.controller;

import java.io.BufferedReader;
import java.io.IOException;
 
import java.io.InputStreamReader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.examples.oauth2.Log;
import com.util.WeiboOauthClient;
import com.weibo4j.Oauth;
import com.weibo4j.http.AccessToken;
import com.weibo4j.model.WeiboException;
import com.weibo4j.util.BareBonesBrowserLaunch;
import com.util.WeiboOauthClient;
 

@Controller
@RequestMapping("/weibo")
public class WeiBoController extends BaseController{

	
	@RequestMapping(value="/getAccess_token")
	public void getAccessToken(String code){
 
		System.out.println("code="+code);
		
		
		AccessToken accessToken = null;
		Oauth oauth = new Oauth();
		try {
			accessToken=oauth.getAccessTokenByCode(code);
		} catch (WeiboException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String accessTokenStr;
		String uid;
		String openId;
		
 		WeiboOauthClient weiboOauthClient=WeiboOauthClient.getInstance();
//	    accessTokenStr = weiboOauthClient.getAccessToken(code);
//		openId = weiboOauthClient.getOpenId(accessToken);
	   
		accessTokenStr=accessToken.getAccessToken();
		uid=accessToken.getUid();
		openId = weiboOauthClient.getOpenId(accessTokenStr);
		
	    System.out.println("accessToken="+accessTokenStr);
	    System.out.println("uid="+uid);
		System.out.println("code="+code);
		
	 
	}
	
	@RequestMapping(value="/test")
	public void test(){
		
		System.out.println("test");
		
		
	}
	
	
 
	public static void main(String[] args) throws WeiboException, IOException {
 
		Oauth oauth = new Oauth();
		BareBonesBrowserLaunch.openURL(oauth.authorize("code"));
 
	}
}
