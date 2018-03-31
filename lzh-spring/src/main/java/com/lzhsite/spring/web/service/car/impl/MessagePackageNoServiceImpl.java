package com.lzhsite.spring.web.service.car.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.lzhsite.spring.web.entity.car.MessagePackageNo;
import com.lzhsite.spring.web.mapper.car.MessagePackageNoMapper;
import com.lzhsite.spring.web.service.car.MessagePackageNoService;

/**
 * <p>
 * 消息包编号表 服务实现类
 * </p>
 *
 * @author lcj
 * @since 2017-09-25
 */
@Service
public class MessagePackageNoServiceImpl  implements MessagePackageNoService {
	@Autowired
	private MessagePackageNoMapper messagePackageNoMapper;
	
	public void insert(MessagePackageNo messagePackageNo){
		messagePackageNoMapper.insert(messagePackageNo);
	}

	@Override
	public MessagePackageNo selectOne(Wrapper<MessagePackageNo> eq) {
		// TODO Auto-generated method stub
		return messagePackageNoMapper.selectOne(eq);
	}
}
