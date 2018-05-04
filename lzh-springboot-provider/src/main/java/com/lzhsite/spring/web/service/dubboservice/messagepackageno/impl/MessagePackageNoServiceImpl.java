package com.lzhsite.spring.web.service.dubboservice.messagepackageno.impl;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.codingapi.tx.annotation.TxTransaction;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.mapper.db0.message.MessagePackageNoMapper;
import com.lzhsite.spring.web.service.messagepackageno.MessagePackageNoService;

/**
 * <p>
 * 消息包编号表 服务实现类
 * </p>
 *
 * @author lcj
 * @since 2017-09-25
 */
@Service(version = "1.0.0", timeout = 10000)
public class MessagePackageNoServiceImpl implements MessagePackageNoService {
	//@Resource默认按照名称方式进行bean匹配，@Autowired默认按照类型方式进行bean匹配
	@Autowired
	private MessagePackageNoMapper messagePackageNoMapper;

	//@Transactional("dataSourceDB0")
	@Override
	@Transactional
	@TxTransaction
	public void insert(MessagePackageNo messagePackageNo) {
		messagePackageNoMapper.insert(messagePackageNo);
	}

	@Override
	public MessagePackageNo selectOne(Wrapper<MessagePackageNo> eq) {
		// TODO Auto-generated method stub
		System.out.println(eq.getSqlSegment());
		return messagePackageNoMapper.selectOne(eq);
	}
}
