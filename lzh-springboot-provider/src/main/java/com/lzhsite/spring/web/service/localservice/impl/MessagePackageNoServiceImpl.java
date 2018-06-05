package com.lzhsite.spring.web.service.localservice.impl;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.codingapi.tx.annotation.TxTransaction;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.mapper.db0.message.MessagePackageNoMapper;
import com.lzhsite.spring.web.service.localservice.MessagePackageNoService;

/**
 * <p>
 * 消息包编号表 服务实现类
 * </p>
 *
 * @author lzhcode
 * @since 2018-06-05
 */
@Service
public class MessagePackageNoServiceImpl implements MessagePackageNoService {
 
	@Autowired
	private MessagePackageNoMapper messagePackageNoMapper;

	@Transactional(value="transactionManager0")
    @TxTransaction
	//@Transactional注释的value属性不是必需的。如果没有提到，Spring将默认查看在上下文中声明的名称为"transactionManager"
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
