package com.lzhsite.webkits.secret.services;

import com.lzhsite.enums.SignType;
import com.lzhsite.webkits.secret.entities.Secret;

/**
 * Created by nt on 2015-06-29.
 */
public interface SecretService {

    Secret getSecret(String id, SignType type);

    Boolean isInnerApplications(String appId, SignType signType);

}
