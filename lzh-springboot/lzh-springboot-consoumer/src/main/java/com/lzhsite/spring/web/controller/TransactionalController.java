package com.lzhsite.spring.web.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lzhsite.spring.util.Result;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.service.localservice.GroupService;

/**
 * @Project: design-pattern
 * @Package: com.lcj.controller
 * @Author: Administrator
 * @Description: TODO
 * @Version: 1.0
 * @Datetime: 2017/9/27 16:19
 */
@RestController
@RequestMapping
public class TransactionalController {
	
	 // 可以测试分布式事务
    @Autowired
    private GroupService  groupService;
 

    @GetMapping("/index")
    public Result index(){
        // 构造数据
        MessagePackageNo messagePackageNo = new MessagePackageNo();
        messagePackageNo.setNo(99);
        messagePackageNo.setCreateTime(new Date());

        User user = new User();
        user.setAge(25);
        user.setName("lcj");
        user.setCreateTime(new Date());

        try {
			groupService.save(messagePackageNo,user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new Result();
        //return new Result().setData(groupService.findByNo(1));
    }
}
