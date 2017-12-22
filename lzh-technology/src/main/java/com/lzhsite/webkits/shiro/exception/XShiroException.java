package com.lzhsite.webkits.shiro.exception;

import org.apache.shiro.authc.AuthenticationException;

/**
 * shiro 统一的exception
 * @author Guoqw
 * @since 2015-09-21 15:48
 */
public class XShiroException extends AuthenticationException {

    private static final long serialVersionUID = 4426407427116328390L;

    public static final String DEFAULT_FAULT_CODE = "SHIRO_SYSTEM_0001";

    private String code;
    private String message;


    public XShiroException(String message) {
        this(DEFAULT_FAULT_CODE, message);
    }

    public XShiroException(String code, String message) {
        this(code, message, new Throwable());
    }

    public XShiroException(String code, String message,String internalMessage) {
        this(code, message, internalMessage, null);
    }

    public XShiroException(String code, String message, Throwable throwable) {
        this(code, message, throwable.getMessage(), throwable);
    }

    public XShiroException(String code, String message, String internalMessage, Throwable throwable) {
        super("[" + code + "] - [" + message +"]" + internalMessage, throwable);
        this.message = message;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
