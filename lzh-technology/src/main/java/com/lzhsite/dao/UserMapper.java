package com.lzhsite.dao;

import java.util.List;

import com.lzhsite.core.datasource.DataSource;
import com.lzhsite.core.orm.mybatis.BaseMapper;
import com.lzhsite.entity.User;

public interface UserMapper extends BaseMapper<User> {

	@DataSource("slave")
	public List<User> getUserlist();


	@DataSource("master")
    public User loadbyname(String name);
	
    public User loadbyid(Integer id);
}
