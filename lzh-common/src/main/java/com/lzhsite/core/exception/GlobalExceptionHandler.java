package com.lzhsite.core.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OverLimitException.class)
    @ResponseBody
    public String OverLimitExceptionHandler(OverLimitException ole){
        return ole.getMessage();
    }
}