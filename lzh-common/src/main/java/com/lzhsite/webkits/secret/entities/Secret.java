package com.lzhsite.webkits.secret.entities;

/**
 * Created by Jintao on 2015/11/27.
 */

import java.io.Serializable;

/**
 * 当采用md5加密的时候，clientPublicKey 与 xkeshiPublicKey相同；
 * 当采用rsa/dsa加密时候，clientPublicKey由开发者/商户上传，xkeshiPublicKey为爱客仕对外提供的公钥
 */
public class Secret implements Serializable {

    private static final long serialVersionUID = 9173498405392480615L;

    /**
     * 开发者公钥，由开发者上传提供（当加密方式为md5时候，为md5加密key）
     */
    private String clientPublicKey;

    /**
     * 爱客仕私钥，仅供爱客仕对内使用（当加密方式为md5时候，为md5加密key）
     */
    private String xkeshiPrivateKey;

    private Boolean isInner;

    public String getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public String getXkeshiPrivateKey() {
        return xkeshiPrivateKey;
    }

    public void setXkeshiPrivateKey(String xkeshiPublicKey) {
        this.xkeshiPrivateKey = xkeshiPublicKey;
    }

    public Boolean getIsInner() {
        return isInner;
    }

    public void setIsInner(Boolean isInner) {
        this.isInner = isInner;
    }
}
