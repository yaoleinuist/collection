package com.lzhsite.service.Impl;

import java.util.List;

import com.alibaba.dubbo.config.annotation.Service;
import com.lzhsite.dao.UserMapper;
import com.lzhsite.entity.User;
import com.lzhsite.service.DubboProvideService;
@Service
public class DubboProvideServiceImpl implements DubboProvideService{

	public UserMapper userMapper;
	
	@Override
	public List<User> getUserlist() {
		// TODO Auto-generated method stub
		return userMapper.getUserlist();
	}

}
