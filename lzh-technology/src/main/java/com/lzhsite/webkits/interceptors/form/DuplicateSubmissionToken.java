package com.lzhsite.webkits.interceptors.form;

/**
 * 令牌信息类
 * Created by ylc on 2015/7/9.
 */
public final class DuplicateSubmissionToken {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
