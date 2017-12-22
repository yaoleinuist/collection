package com.lzhsite.webkits.session;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义的session会话
 * Created by liuliling on 17/6/13.
 */
public class XSession {

    private String sessionId;
    private Integer expire;
    private Map<String, Object> attributes = new HashMap<>();

    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        if(attributes != null){
            this.attributes = attributes;
        }
    }
}
