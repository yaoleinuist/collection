package com.lzhsite.spring.web.service.dubboservice.messagepackageno.impl;

 
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.service.localservice.MessagePackageNoService;
import com.lzhsite.spring.web.service.messagepackageno.MessagePackageNoWriteService;

/**
 * <p>
 * 消息包编号表 服务实现类
 * </p>
 *
 * @author lcj
 * @since 2017-09-25
 */
@Service(version = "1.0.0", timeout = 10000)
public class MessagePackageNoWriteServiceImpl implements MessagePackageNoWriteService {
	//@Resource默认按照名称方式进行bean匹配，@Autowired默认按照类型方式进行bean匹配


	@Autowired
	private MessagePackageNoService messagePackageNoService;
	
	
	
	public void insert(MessagePackageNo messagePackageNo) {
    	messagePackageNoService.insert(messagePackageNo);
	}

	@Override
	public MessagePackageNo selectOne(Wrapper<MessagePackageNo> eq) {
		// TODO Auto-generated method stub
		System.out.println(eq.getSqlSegment());
		return messagePackageNoService.selectOne(eq);
	}
}
