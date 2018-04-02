package com.lzhsite.spring.web.service.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.service.messagepackageno.MessagePackageNoService;
import com.lzhsite.spring.web.service.user.UserService;

/**
 * @Project: design-pattern
 * @Package: com.lcj.service.impl
 * @Author: Administrator
 * @Description: TODO
 * @Version: 1.0
 * @Datetime: 2017/9/27 17:35
 */
@Service
public class IndexService {
    @Autowired
    private MessagePackageNoService messagePackageNoService;

    @Autowired
    private UserService userService;

    /**
     * 保存数据
     *
     * @param messagePackageNo
     * @param user
     */
    @Transactional
    public void save(MessagePackageNo messagePackageNo, User user) {
        messagePackageNoService.insert(messagePackageNo);
        userService.insert(user);
        int i = 4 / 0; // 除0异常,测试事务
        System.out.println("aaa");
    }

    /**
     * @method: findByNo
     * @param: [no]
     * @description: 根据no查询
     * @author: Administrator
     * @date: 2017/9/28
     * @time: 13:21
     * @return: com.lcj.web.entity.car.MessagePackageNo
     */
    public MessagePackageNo findByNo(int no) {
        return messagePackageNoService.selectOne(new EntityWrapper<MessagePackageNo>().eq("no",no));
    }
}
