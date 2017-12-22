package com.lzhsite.webkits.security.signature;

import java.util.List;

/**
 * Created by ruancl@xkeshi.com on 2017/6/20.
 * Updated by liuliling on 2017/6/22.
 */
public class SignatureConfig {

    /**
     * 应用类型,如:APP、WAP、WXAPP
     */
    private String type;

    /**
     * 跳过签名
     */
    private Boolean skipCheckSignature = Boolean.FALSE;

    /**
     * 是否跳过response中写入签名
     */
    private Boolean skipWriteSignature = Boolean.FALSE;

    /**
     * 签名校验 可以忽略的path
     */
    private List<String> ignoreList;

    /**
     * 跳过签名设置为true 时候 signatureService将不会执行verify
     */
    private SignatureService signatureService;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getSkipCheckSignature() {
        return skipCheckSignature;
    }

    public void setSkipCheckSignature(Boolean skipCheckSignature) {
        if(skipCheckSignature != null){
            this.skipCheckSignature = skipCheckSignature;
        }
    }

    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public void setIgnoreList(List<String> ignoreList) {
        this.ignoreList = ignoreList;
    }

    public SignatureService getSignatureService() {
        return signatureService;
    }

    public void setSignatureService(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    public Boolean getSkipWriteSignature() {
        return skipWriteSignature;
    }

    public void setSkipWriteSignature(Boolean skipWriteSignature) {
        this.skipWriteSignature = skipWriteSignature;
    }
}
