package com.lzhsite.entity;

import javax.persistence.*;

/**
 * Created by Jintao on 2017/3/22.
 */
@Entity
@Table(name = "secret")
public class Secret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "app_name")
    private String appName;

    @Column(name = "sign_type")
    private String signType;

    @Column(name="is_inner")
    private Boolean isInner;

    @Column(name="app_secret")
    private String appSecret;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public Boolean getIsInner() {
        return isInner;
    }

    public void setIsInner(Boolean isInner) {
        this.isInner = isInner;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
