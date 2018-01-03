package com.lzhsite.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/websocket")
public class WebSocketController  extends BaseController{
	
    private final String prefix = "websocket/";
	
	@RequestMapping(value="/index")
	public String index() {
		
		 return prefix + "index";
		
	}
}
