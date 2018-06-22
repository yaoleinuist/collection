package com.lzhsite.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lzhsite.core.utils.JsonUtil;
import com.lzhsite.dao.UserMapper;
import com.lzhsite.entity.User;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController{

    private final String prefix = "user/";
	
    @Resource
	public UserMapper userMapper;
 
	@RequestMapping(value="/listPmsUser")
	@ResponseBody
	public String listPmsUser() {
		List<User> list= new ArrayList<>();
		try {
			Map<String, Object> paramMap = new HashMap<String, Object>(); // 业务条件查询参数
			paramMap.put("phone", getString("phone")); // 用户姓名（模糊查询）
			paramMap.put("name", getString("name")); // 用户姓名（模糊查询）
			paramMap.put("status", getInteger("status")); // 状态
	 
		    list=userMapper.getUserlist();
			logger.info("用户列表-----"+JsonUtil.toJson(list));
			
			User user=userMapper.loadbyname("管理员");
			logger.info("name='管理员'管理员-----"+JsonUtil.toJson(user));
			
			user=userMapper.loadbyid(1);
			logger.info("id=1的用户-----"+JsonUtil.toJson(user));
			
		} catch (Exception e) {
			logger.error("== listPmsUser exception:", e);
		 
		}
		
		   return JsonUtil.toJson(list);
	}
}
