package com.lzhsite.core.output;

/**
 * Created by Jintao on 2015/6/2.
 */
public class Result {

    public Result() {

    }

    public Result(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public Result(String code, String description, Object result) {
        this.code = code;
        this.description = description;
        this.result = result;
    }

    private String code;

    private String description;

    private Object result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
