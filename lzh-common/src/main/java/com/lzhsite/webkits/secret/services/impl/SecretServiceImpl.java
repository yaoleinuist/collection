package com.lzhsite.webkits.secret.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.lzhsite.ensure.Ensure;
import com.lzhsite.enums.SignType;
import com.lzhsite.webkits.secret.dao.SecretDAO;
import com.lzhsite.webkits.secret.entities.Secret;
import com.lzhsite.webkits.secret.entities.SecretEntity;
import com.lzhsite.webkits.secret.services.SecretService;

/**
 * Created by nt on 2015-06-29.
 */
@Service
public class SecretServiceImpl implements SecretService {

    private static final Logger logger = LoggerFactory.getLogger(SecretServiceImpl.class);

    @Autowired
    private SecretDAO secretDAO;

    @Value("${xkeshi.rsa.private.key}")
    private String xkeshiRsaPrivateKey;

    @Value("${xkeshi.dsa.private.key}")
    private String xkeshiDsaPrivateKey;


    @Override
    @Cacheable(value = "secret")
    public Secret getSecret(String appId, SignType type) {

        logger.info("get secret from database.");

        SecretEntity secretEntity = secretDAO.getSecret(appId, type.getValue());
        Ensure.that(secretEntity).isNotNull("F_SERVICES_SECRET_1002");

        Secret secret = new Secret();
        switch(type){
            case RSA:
                secret.setClientPublicKey(secretEntity.getAppSecret());
                secret.setXkeshiPrivateKey(xkeshiRsaPrivateKey);
                secret.setIsInner(secretEntity.getIsInner());
                return secret;
            case DSA:
                secret.setClientPublicKey(secretEntity.getAppSecret());
                secret.setXkeshiPrivateKey(xkeshiDsaPrivateKey);
                secret.setIsInner(secretEntity.getIsInner());
                return secret;
            case MD5:
            default:
                secret.setClientPublicKey(secretEntity.getAppSecret());
                secret.setXkeshiPrivateKey(secretEntity.getAppSecret());
                secret.setIsInner(secretEntity.getIsInner());
                return secret;
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
        Secret secret = this.getSecret(appId, signType);
        return secret.getIsInner();
    }

}
