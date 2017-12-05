package com.lzhsite.exception;

import com.alibaba.dubbo.rpc.RpcException;

/**
 * Created by nt on 2015-05-28.
 */
public class XRuntimeException extends RpcException {

    public XRuntimeException() {
    }

    public XRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public XRuntimeException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

}
