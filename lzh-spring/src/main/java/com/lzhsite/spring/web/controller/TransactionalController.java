package com.lzhsite.spring.web.controller;

import com.lzhsite.spring.util.Result;
import com.lzhsite.spring.web.config.DataSourceCarProperties;
import com.lzhsite.spring.web.entity.car.MessagePackageNo;
import com.lzhsite.spring.web.entity.test.User;
import com.lzhsite.spring.web.service.group.IndexService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

    @Autowired
    private IndexService indexService; // 可以测试分布式事务

    @Autowired
    private DataSourceCarProperties dataSourceCarProperties; // 自定义属性映射到POJO对象

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

        indexService.save(messagePackageNo,user);

        return new Result().setData(indexService.findByNo(1));
    }
}
