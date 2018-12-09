package com.lzhsite.spring.web.service.dubboservice.user.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.service.localservice.UserService;
import com.lzhsite.spring.web.service.user.UserWriteService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lzhcode
 * @since 2017-09-27
 */
@Service(version = "1.0.0", timeout = 10000)
public class UserServiceWriteImpl implements UserWriteService {
	
	@Autowired
	private UserService userService;

	@Override
	public void insert(User user) {
		userService.insert(user);
	}
}
