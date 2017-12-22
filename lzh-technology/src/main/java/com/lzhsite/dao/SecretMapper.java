package com.lzhsite.dao;

import org.apache.ibatis.annotations.Param;

import com.lzhsite.core.orm.mybatis.BaseMapper;
import com.lzhsite.entity.Secret;

/**
 * Created by nt on 2015-06-29.
 */
public interface SecretMapper extends BaseMapper<Secret>{

    Secret getSecret(@Param("appId") String appId, @Param("signType") String signType);

}
