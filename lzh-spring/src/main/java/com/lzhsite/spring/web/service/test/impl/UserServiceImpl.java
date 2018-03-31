package com.lzhsite.spring.web.service.test.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lzhsite.spring.web.entity.test.User;
import com.lzhsite.spring.web.mapper.test.UserMapper;
import com.lzhsite.spring.web.service.test.UserService;

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
