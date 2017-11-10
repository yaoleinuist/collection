package com.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dao.UserMapper;
import com.util.page.PageBean;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

    private final String prefix = "user/";
	
    @Resource
	public UserMapper userMapper;
 
	@RequestMapping(value="/listPmsUser")
	public String listPmsUser() {
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>(); // 业务条件查询参数
			paramMap.put("phone", getString("phone")); // 用户姓名（模糊查询）
			paramMap.put("name", getString("name")); // 用户姓名（模糊查询）
			paramMap.put("status", getInteger("status")); // 状态
		} catch (Exception e) {
			logger.error("== listPmsUser exception:", e);
		}
		
		   return prefix+"user_list3";
	}

	public  void test(){


	}
}
