package com.lzhsite.spring.util;

/**
 * Created by Administrator on 2017/4/25.
 */
public class Result<T> {
   public static final Integer OK = 0;
   public static final Integer ERROR = -1;


    private Integer code = OK;

    private String msg = "success";

    private T data;

    public Result() {
    }

    public Result(Integer code, String msg) {
        this(code,msg,null);
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"code\":")
                .append(code);
        sb.append(",\"msg\":\"")
                .append(msg).append('\"');
        sb.append(",\"data\":")
                .append(data);
        sb.append('}');
        return sb.toString();
    }
}
