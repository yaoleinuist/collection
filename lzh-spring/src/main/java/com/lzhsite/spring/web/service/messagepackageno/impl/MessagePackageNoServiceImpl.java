package com.lzhsite.spring.web.service.messagepackageno.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.mapper.db0.car.MessagePackageNoMapper;
import com.lzhsite.spring.web.service.messagepackageno.MessagePackageNoService;

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
		System.out.println(eq.getSqlSegment());
		return messagePackageNoMapper.selectOne(eq);
	}
}
