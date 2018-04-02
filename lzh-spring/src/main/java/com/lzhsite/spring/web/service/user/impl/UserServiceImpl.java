package com.lzhsite.spring.web.service.user.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.mapper.db1.user.UserMapper;
import com.lzhsite.spring.web.service.user.UserService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lcj
 * @since 2017-09-27
 */
@Service
public class UserServiceImpl  implements UserService {
	@Autowired
    private UserMapper userMapper;
	
	public void insert(User user){
		userMapper.insert(user);
	}
}
