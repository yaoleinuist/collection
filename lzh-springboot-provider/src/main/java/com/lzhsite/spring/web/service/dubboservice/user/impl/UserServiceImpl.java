package com.lzhsite.spring.web.service.dubboservice.user.impl;

 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.codingapi.tx.annotation.TxTransaction;
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
@Service(version = "1.0.0",timeout = 10000)
public class UserServiceImpl  implements UserService {
	//@Autowired
   // private UserMapper userMapper;
	//@Transactional("dataSourceDB1")
	@Transactional
	@TxTransaction
	public void insert(User user){
		//userMapper.insert(user);
	}
}
