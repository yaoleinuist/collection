package com.lzhsite.spring.web.service.localservice;


import com.lzhsite.spring.web.entity.db0.MessagePackageNo;
import com.lzhsite.spring.web.entity.db1.User;

public interface IndexService {
	 
    public void save(MessagePackageNo messagePackageNo, User user);
 
    public MessagePackageNo findByNo(int no);
}
