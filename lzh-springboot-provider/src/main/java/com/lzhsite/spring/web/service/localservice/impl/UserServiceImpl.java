package com.lzhsite.spring.web.service.localservice.impl;

 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codingapi.tx.annotation.TxTransaction;
import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.mapper.db1.user.UserMapper;
import com.lzhsite.spring.web.service.localservice.UserService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lzhcode
 * @since 2018-06-05
 */
@Service 
public class UserServiceImpl  implements UserService {
	
	@Autowired
    private UserMapper userMapper;
    
    
//  LCN无法自己切换数据源,getConnetcion还是原数据源的连接    
//	@Transactional(value="transactionManager1")
//	@TxTransaction
	public void insert(User user){
		userMapper.insert(user);
	}
}
