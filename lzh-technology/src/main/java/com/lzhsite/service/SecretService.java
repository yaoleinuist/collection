package com.lzhsite.service;

import com.lzhsite.core.utils.enums.SignType;
import com.lzhsite.dto.SecretDto;
import com.lzhsite.entity.Secret;

/**
 * Created by nt on 2015-06-29.
 */
public interface SecretService {

    SecretDto getSecret(String id, SignType type);

    Boolean isInnerApplications(String appId, SignType signType);

}
