package com.lzhsite.core.exception;

import com.alibaba.dubbo.rpc.RpcException;

/**
 * Created by nt on 2015-05-28.
 */
public class XRuntimeException  extends RuntimeException {

    public XRuntimeException() {
    }

    public XRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
 

}
