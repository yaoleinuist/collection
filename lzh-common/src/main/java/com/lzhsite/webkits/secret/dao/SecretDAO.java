package com.lzhsite.webkits.secret.dao;

import org.apache.ibatis.annotations.Param;

import com.lzhsite.webkits.secret.entities.SecretEntity;

/**
 * Created by nt on 2015-06-29.
 */
public interface SecretDAO {

    SecretEntity getSecret(@Param("appId") String appId, @Param("signType") String signType);

}
