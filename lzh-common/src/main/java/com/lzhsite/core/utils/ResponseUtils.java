package com.lzhsite.core.utils;

import com.lzhsite.core.exception.XBusinessException;
import com.lzhsite.core.output.Result;

/**
 * Created by Jintao on 2015/5/20.
 */
public class ResponseUtils {

    private static final String FAULT_CODE = "-1";
    private static final String SUCCESS_CODE = "0";

    private static final String SUCCESS_MSG = "OK";
    private static final String FAULT_MSG = "服务器内部错误";
    private static final String UNKNOWN_MSG = "服务器内部未知错误";

    public static Result getXBusinessResult(XBusinessException ex){
        return new Result(ex.getXCode(), ex.getMessageWithoutCode());
    }

    public static Result getFaultResult() {
        return new Result(FAULT_CODE, FAULT_MSG);
    }

    public static Result getSuccessResult(Object obj){
        return new Result(SUCCESS_CODE, SUCCESS_MSG, obj);
    }

    public static Result getUnknownResult(){
        return new Result(FAULT_CODE, UNKNOWN_MSG);
    }
}
