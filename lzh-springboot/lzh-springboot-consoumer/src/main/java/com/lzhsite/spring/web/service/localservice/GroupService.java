package com.lzhsite.spring.web.service.localservice;

import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.entity.db1.User;

public interface GroupService {
	/**
	 * 保存数据
	 *
	 * @param messagePackageNo
	 * @param user
	 */
	public void save(MessagePackageNo messagePackageNo, User user);

	/**
	 * @method: findByNo
	 * @param: [no]
	 * @description: 根据no查询
	 * @author: Administrator
	 * @date: 2017/9/28
	 * @time: 13:21
	 * @return: com.lcj.web.entity.car.MessagePackageNo
	 */
	public MessagePackageNo findByNo(int no);
}
