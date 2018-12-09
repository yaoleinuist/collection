package com.lzhsite.spring.web.service.localservice;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;

/**
 * <p>
 * 消息包编号表 服务类
 * </p>
 *
 * @author lzhcode
 * @since 2018-06-05
 */
public interface MessagePackageNoService {
	
	void insert(MessagePackageNo messagePackageNo);

	MessagePackageNo selectOne(Wrapper<MessagePackageNo> eq);
}
