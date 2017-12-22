package com.lzhsite.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lzhsite.core.ensure.Ensure;
import com.lzhsite.core.utils.enums.SignType;
import com.lzhsite.dao.SecretMapper;
import com.lzhsite.dto.SecretDto;
import com.lzhsite.entity.Secret;
import com.lzhsite.service.SecretService;
 

/**
 * Created by nt on 2015-06-29.
 */
@Service
public class SecretServiceImpl implements SecretService {

    private static final Logger logger = LoggerFactory.getLogger(SecretServiceImpl.class);

    @Autowired
    private SecretMapper secretDAO;

    @Value("${lzhsite.rsa.private.key}")
    private String lzhsiteRsaPrivateKey;

    @Value("${lzhsite.dsa.private.key}")
    private String lzhsiteDsaPrivateKey;


    @Override
    @Cacheable(value = "secret")
    public SecretDto getSecret(String appId, SignType type) {

        logger.info("get secret from database.");

        Secret secretEntity = secretDAO.getSecret(appId, type.getValue());
        Ensure.that(secretEntity).isNotNull("F_SERVICES_SECRET_1002");

        SecretDto secretDto = new SecretDto();
        switch(type){
            case RSA:
            	secretDto.setClientPublicKey(secretEntity.getAppSecret());
            	secretDto.setXkeshiPrivateKey(lzhsiteRsaPrivateKey);
            	secretDto.setIsInner(secretEntity.getIsInner());
                return secretDto;
            case DSA:
            	secretDto.setClientPublicKey(secretEntity.getAppSecret());
                secretDto.setXkeshiPrivateKey(lzhsiteDsaPrivateKey);
                secretDto.setIsInner(secretEntity.getIsInner());
                return secretDto;
            case MD5:
            default:
            	secretDto.setClientPublicKey(secretEntity.getAppSecret());
            	secretDto.setXkeshiPrivateKey(secretEntity.getAppSecret());
                secretDto.setIsInner(secretEntity.getIsInner());
                return secretDto;
        }
    }

    /**
     * 判断是否为内部应用
     * @param appId
     * @param signType
     * @return
     */
    @Override
    public Boolean isInnerApplications(String appId, SignType signType) {
    	SecretDto secretDto = this.getSecret(appId, signType);
        return secretDto.getIsInner();
    }

}
