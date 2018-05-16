package com.lzhsite.service.Impl;

import java.util.List;

import javax.annotation.PostConstruct;

import com.alibaba.dubbo.config.annotation.Service;
import com.lzhsite.dao.UserMapper;
import com.lzhsite.entity.User;
import com.lzhsite.service.DubboProvideService;
@Service
public class DubboProvideServiceImpl implements DubboProvideService{

	public UserMapper userMapper;
	
	
	
	/**
     * 构造方法执行之后，调用此方法
     * https://blog.csdn.net/wo541075754/article/details/52174900
     */
    @PostConstruct
    public void init(){
//    	Java EE5 引入了@PostConstruct和@PreDestroy这两个作用于Servlet生命周期的注解，
//    	实现Bean初始化之前和销毁之前的自定义操作
//    	只有一个方法可以使用此注释进行注解；
//    	被注解方法不得有任何参数；
//    	被注解方法返回值为void；
//    	被注解方法不得抛出已检查异常；
//    	被注解方法需是非静态方法；
//    	此方法只会被执行一次；
        System.out.println("@PostConstruct方法被调用");
    }

	
	@Override
	public List<User> getUserlist() {
		// TODO Auto-generated method stub
		return userMapper.getUserlist();
	}

}
