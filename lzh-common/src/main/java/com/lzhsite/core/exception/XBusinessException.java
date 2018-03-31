package com.lzhsite.core.exception;

import com.alibaba.dubbo.rpc.RpcException;

/**
 * Created by Jintao on 2015/5/20.
 */
public class XBusinessException extends XRuntimeException {

    public static final String DEFAULT_FAULT_CODE = "X0001";

    private String xCode;
    private String message;

    public XBusinessException(String message){
        this(DEFAULT_FAULT_CODE, message);
    }

    public XBusinessException(String xCode, String message) {
        this(xCode, message, new Throwable());
    }

    public XBusinessException(String xCode, String message, String internalMessage) {
        this(xCode, message, internalMessage, null);
    }

    public XBusinessException(String code, String message, Throwable throwable) {
        this(code, message, throwable.getMessage(), throwable);
    }

    public XBusinessException(String xCode, String message, String internalMessage, Throwable throwable) {
        this.message = message;
        this.xCode = xCode;
    }

    public String getXCode() {
        return xCode;
    }

    public void setXCode(String xCode) {
        this.xCode = xCode;
    }

    public String getMessageWithoutCode(){
        return message;
    }

    @Override
    public String getMessage() {
        return "[" + xCode + "]" + " - " + message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
