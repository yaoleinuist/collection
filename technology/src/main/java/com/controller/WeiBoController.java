package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/weibo")
public class WeiBoController extends BaseController{

	
	@RequestMapping(value="/getAccess_token")
	public void getAccessToken(){
		
		System.out.println("getAccess_token");
	}
}
