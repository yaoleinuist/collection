package com.lzhsite.exception;

import com.lzhsite.context.SpringContextHolder;

/**
 * Created by Jintao on 2015/6/9.
 */
public class XExceptionFactory {

    private static ExceptionDefinitions exceptionDefinitions;

    public static XBusinessException create(String errorCode, String...args){
        String exceptionPattern = getExceptionDefinitions().getExceptionMessage(errorCode);

        if(args.length > 0){
            String errorMsg = String.format(exceptionPattern, args);
            return new XBusinessException(errorCode,errorMsg);
        }
        return new XBusinessException(errorCode,exceptionPattern);
    }

    private static ExceptionDefinitions getExceptionDefinitions(){
        if(exceptionDefinitions == null){
            exceptionDefinitions = SpringContextHolder.getApplicationContext().getBean(ExceptionDefinitions.class);
        }
        return exceptionDefinitions;
    }
}
