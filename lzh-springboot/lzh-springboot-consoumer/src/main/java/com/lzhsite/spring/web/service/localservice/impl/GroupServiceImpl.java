package com.lzhsite.spring.web.service.localservice.impl;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.codingapi.tx.annotation.TxTransaction;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.entity.db1.User;
import com.lzhsite.spring.web.service.localservice.GroupService;
import com.lzhsite.spring.web.service.messagepackageno.MessagePackageNoWriteService;
import com.lzhsite.spring.web.service.user.UserWriteService;

/**
 * @Project: design-pattern
 * @Author: Administrator
 * @Description: TODO
 * @Version: 1.0
 * @Datetime: 2017/9/27 17:35
 */
@Service
public class GroupServiceImpl implements GroupService {
	@Reference(version = "1.0.0")
	private MessagePackageNoWriteService messagePackageNoWriteService;

	@Reference(version = "1.0.0")
	private UserWriteService userWriteService;

	/**
	 * 保存数据
	 *
	 * @param messagePackageNo
	 * @param user
	 */
	@Override
	@TxTransaction(isStart = true)
	//@Transactional
	public void save(MessagePackageNo messagePackageNo, User user){
		messagePackageNoWriteService.insert(messagePackageNo);
		userWriteService.insert(user);
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
		return messagePackageNoWriteService.selectOne(new EntityWrapper<MessagePackageNo>().eq("no", no));
	}
}
