package com.lzhsite.spring.transtation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.lzhsite.spring.Application;
import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.service.user.UserWriteService;

/**
 * Created by lzhcode on 2018/6/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TranstationTest {


	@Autowired
	private UserWriteService userService;

	@Test
	@Transactional
	public void test1() throws Exception {
		try {
			User user=new User();
			user.setAge(11);
			user.setName("lzhtest");
			userService.insert(user);
            int a=1/0;
		} catch (Exception e) {
			throw new  Exception("执行事务时出错");
		}
	}

}
