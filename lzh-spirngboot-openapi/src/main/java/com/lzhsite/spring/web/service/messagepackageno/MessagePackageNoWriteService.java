package com.lzhsite.spring.web.service.messagepackageno;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.lzhsite.spring.web.entity.db0.MessagePackageNo;

/**
 * <p>
 * 消息包编号表 服务类
 * </p>
 *
 * @author lcj
 * @since 2017-09-25
 */
public interface MessagePackageNoWriteService {
	void insert(MessagePackageNo messagePackageNo);

	MessagePackageNo selectOne(Wrapper<MessagePackageNo> eq);
}
