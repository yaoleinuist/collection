package com.lzhsite.spring.web.service.localservice.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.codingapi.tx.annotation.TxTransaction;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.service.localservice.IndexService;
import com.lzhsite.spring.web.service.messagepackageno.MessagePackageNoWriteService;
import com.lzhsite.spring.web.service.user.UserWriteService;
@Service
public class IndexServiceImpl implements IndexService{

	
    @Autowired
    private MessagePackageNoWriteService messagePackageNoWriteService;

    @Autowired
    private UserWriteService userService;
	 /**
     * 保存数据
     *
     * @param messagePackageNo
     * @param user
     */
    @TxTransaction(isStart = true)
    public void save(MessagePackageNo messagePackageNo, User user) {
    	messagePackageNoWriteService.insert(messagePackageNo);
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
        return messagePackageNoWriteService.selectOne(new EntityWrapper<MessagePackageNo>().eq("no",no));
    }

}
