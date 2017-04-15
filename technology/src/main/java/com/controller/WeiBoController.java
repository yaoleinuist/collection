package com.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.util.WeiboOauthClient;
 

@Controller
@RequestMapping("/weibo")
public class WeiBoController extends BaseController{

	
	@RequestMapping(value="/getAccess_token")
	public void getAccessToken(String code){
		
		System.out.println("getAccess_token");
		System.out.println("code="+code);
		
		WeiboOauthClient weiboOauthClient=WeiboOauthClient.getInstance();
 
 
	    String accessToken = weiboOauthClient.getAccessToken(code);
		String openId = weiboOauthClient.getOpenId(accessToken);
	   
	    System.out.println("accessToken="+accessToken);
	    System.out.println("openId="+openId);
		
	}
	
	@RequestMapping(value="/test")
	public void test(){
		
		System.out.println("test");
		
		
	}
	
	
	public static void main(String[] args) {
 
		
	}
}
